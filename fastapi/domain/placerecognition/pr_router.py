from fastapi import File, UploadFile, APIRouter, HTTPException
import torch
from torchvision import transforms
from PIL import Image, ExifTags
import io
import os
from .utils.util_model import EmbedNet, TripletNet
from .backbones import get_backbone
from .models import get_model

def load_model():
    backbone = get_backbone('resnet18')
    model = get_model('netvlad')
    embed_net = EmbedNet(backbone, model)
    triplet_net = TripletNet(embed_net).to(device)
    checkpoint = torch.load('C:/Users/user/Desktop/PR/backend/model-api/fastapi/domain/placerecognition/models/resnet18_netvlad_nordland_checkpoint_e10.pth', map_location=device)
    new_state_dict = {key.replace('module.', ''): value for key, value in checkpoint.items()}
    triplet_net.load_state_dict(new_state_dict, strict=False)
    triplet_net.eval()
    for param in triplet_net.parameters():
        param.requires_grad = False

    preprocess = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ])

    return triplet_net, preprocess

def correct_image_orientation(image):
    try:
        for orientation in ExifTags.TAGS.keys():
            if ExifTags.TAGS[orientation] == 'Orientation':
                break
        exif = image._getexif()
        if exif is not None:
            exif = dict(exif.items())
            orientation = exif.get(orientation)
            if orientation == 2:
                image = image.transpose(Image.FLIP_LEFT_RIGHT)
            elif orientation == 3:
                image = image.rotate(180, expand=True)
            elif orientation == 4:
                image = image.transpose(Image.FLIP_TOP_BOTTOM)
            elif orientation == 5:
                image = image.transpose(Image.FLIP_LEFT_RIGHT).rotate(270, expand=True)
            elif orientation == 6:
                image = image.rotate(270, expand=True)
            elif orientation == 7:
                image = image.transpose(Image.FLIP_LEFT_RIGHT).rotate(90, expand=True)
            elif orientation == 8:
                image = image.rotate(90, expand=True)
    except Exception as e:
        print(f"Error correcting image orientation: {e}")
    return image

def prepare_image(image_file):
    image = Image.open(io.BytesIO(image_file)).convert('RGB')
    image = correct_image_orientation(image)
    image = preprocess(image)
    image = image.unsqueeze(0)
    return image

def load_building_images():
    building_images = {}
    building_numbers = {
        '1': '구두',
        '2': '새천',
        '4': '승연관',
        '5': '미카정보',
        '6': '중도',
        '8': '일만'
    }
    base_path = 'C:/Users/user/Desktop/PR/backend/model-api/fastapi/domain/placerecognition/utils/buildingimg/'
    for number, name in building_numbers.items():
        image_path = os.path.join(base_path, f'{name}.jpg')
        if not os.path.exists(image_path):
            raise Exception(f"Building image {name} with number {number} not found.")
        with open(image_path, 'rb') as f:
            image = f.read()
        building_images[number] = prepare_image(image)
    return building_images

def print_exif_data(image_file):
    image = Image.open(io.BytesIO(image_file))
    exif_data = image._getexif()
    if exif_data:
        exif = {
            ExifTags.TAGS.get(tag): value
            for tag, value in exif_data.items()
            if tag in ExifTags.TAGS
        }
        for key, value in exif.items():
            print(f"{key}: {value}")
    else:
        print("No EXIF data found.")

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model, preprocess = load_model()
building_images = load_building_images()
router = APIRouter(
    prefix="/model",
)

@router.post("/compare/")
async def compare_image_with_building(building_number: str, image: UploadFile = File(...)):
    try:
        img1 = await image.read()
        building_image_tensor = building_images.get(building_number)
        if building_image_tensor is None:
            raise HTTPException(status_code=404, detail=f"Building image with number {building_number} not found.")
        
        print_exif_data(img1)  # Print EXIF data for debugging

    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Error processing request: {e}")

    img1_tensor = prepare_image(img1).to(device)

    model.eval()
    with torch.no_grad():
        features1 = model.feature_extract(img1_tensor)
        features2 = model.feature_extract(building_image_tensor.to(device))
        
        # Check tensor sizes and adjust if necessary
        if features1.shape != features2.shape:
            features1 = torch.nn.functional.adaptive_avg_pool2d(features1, (1, 1)).view(features1.size(0), -1)
            features2 = torch.nn.functional.adaptive_avg_pool2d(features2, (1, 1)).view(features2.size(0), -1)

    distance = torch.nn.functional.pairwise_distance(features1, features2)

    is_similar = 1 if distance.item() < 1 else 0
    return is_similar

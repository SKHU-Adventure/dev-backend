import io
import os

from utils.util_model import LightningTripletNet
from utils.util_function import correct_image_orientation

from fastapi import File, UploadFile, APIRouter, HTTPException
import torch
from torchvision import transforms
from PIL import Image, ExifTags


#0.94로 검사
def load_model():
    triplet_net = LightningTripletNet.load_from_checkpoint("C:\\Users\\user\\Desktop\\PR\\backend\\model_api_최종\\fastapi\\domain\\placerecognition\\models\\last.ckpt", strict=False)

    preprocess = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    ])
    triplet_net.eval()
    return triplet_net, preprocess

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

    
    with torch.no_grad():
        features1 = model.feature_extract(img1_tensor)
        features2 = model.feature_extract(building_image_tensor.to(device))
        
        # Check tensor sizes and adjust if necessary
        if features1.shape != features2.shape:
            features1 = torch.nn.functional.adaptive_avg_pool2d(features1, (1, 1)).view(features1.size(0), -1)
            features2 = torch.nn.functional.adaptive_avg_pool2d(features2, (1, 1)).view(features2.size(0), -1)

    distance =  torch.nn.functional.pairwise_distance(features1, features2)

    is_similar = 1 if distance.item() < 0.94 else 0
    return is_similar

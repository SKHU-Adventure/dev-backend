from fastapi import File, UploadFile, APIRouter, Depends, HTTPException
import torch
from torchvision import transforms
from PIL import Image
import io
import torch.distributed as dist
from .utils.util_model import EmbedNet, TripletNet 
from .backbones import get_backbone 
from .models import get_model

def load_model():
    backbone = get_backbone('resnet18')
    model = get_model('netvlad')
    embed_net = EmbedNet(backbone, model)
    triplet_net = TripletNet(embed_net).to(device)
    checkpoint = torch.load('/home/student4/backend/fastapi/domain/placerecognition/models/resnet18_netvlad_nordland_checkpoint_e10.pth')
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

def prepare_image(image_file):
    image = Image.open(io.BytesIO(image_file)).convert('RGB')
    image = preprocess(image)
    image = image.unsqueeze(0)  
    return image

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model, preprocess = load_model()
router = APIRouter(
    prefix="/model",
)

@router.post("/compare/")
async def compare_images(image1: UploadFile = File(...), image2: UploadFile = File(...)):
    try:
        img1 = await image1.read()
        img2 = await image2.read()
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Error reading files: {e}")

    img1_tensor = prepare_image(img1).to(device)
    img2_tensor = prepare_image(img2).to(device)

    model.eval()
    with torch.no_grad():
        features1 = model.feature_extract(img1_tensor)
        features2 = model.feature_extract(img2_tensor)
    
    distance = torch.nn.functional.pairwise_distance(features1, features2)
    
    is_similar = distance.item() < 1
    return {"is_similar": is_similar}



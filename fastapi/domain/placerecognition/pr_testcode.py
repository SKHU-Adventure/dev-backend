import os
import io
import torch
from torchvision import transforms
from PIL import Image
from utils.util_model import LightningTripletNet

# 모델과 전처리 과정 로드
def load_model():
    model = LightningTripletNet.load_from_checkpoint("C:\\Users\\user\\Desktop\\PR\\backend\\model_api_최종\\fastapi\\domain\\placerecognition\\models\\last.ckpt", strict=False)
    model.eval()
    return model

def prepare_image(image_path, preprocess):
    image = Image.open(image_path).convert('RGB')
    image = preprocess(image)
    image = image.unsqueeze(0)  # 배치 차원 추가
    return image

def compare_images(model, image_path1, image_path2, preprocess):
    img1_tensor = prepare_image(image_path1, preprocess)
    img2_tensor = prepare_image(image_path2, preprocess)

    with torch.no_grad():
        features1 = model.feature_extract(img1_tensor)
        features2 = model.feature_extract(img2_tensor)

    # 텐서 크기 조정 (필요시)
    if features1.shape != features2.shape:
        features1 = torch.nn.functional.adaptive_avg_pool2d(features1, (1, 1)).view(features1.size(0), -1)
        features2 = torch.nn.functional.adaptive_avg_pool2d(features2, (1, 1)).view(features2.size(0), -1)

    distance = torch.nn.functional.pairwise_distance(features1, features2)
    is_similar = 1 if distance.item() < 0.94 else 0  # 거리 기준에 따라 유사성 판단
    return is_similar, distance.item()

# 전처리 정의
preprocess = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
])

# 실행
if __name__ == '__main__':
    model = load_model()

    # 테스트할 두 이미지 경로 입력
    image_path1 = 'C:\\Users\\user\\Desktop\\PR\\backend\\model_api_최종\\fastapi\\domain\\placerecognition\\utils\\buildingimg\\image_0000.jpg'  # 첫 번째 이미지 경로
    image_path2 = 'C:\\Users\\user\\Desktop\\PR\\backend\\model_api_최종\\fastapi\\domain\\placerecognition\\utils\\buildingimg\\image_0008.jpg'  # 두 번째 이미지 경로

    if os.path.exists(image_path1) and os.path.exists(image_path2):
        is_similar, distance = compare_images(model, image_path1, image_path2, preprocess)
        print(f"Distance: {distance}, Are images similar? {'Yes' if is_similar else 'No'}")
    else:
        print("One or both image paths do not exist.")

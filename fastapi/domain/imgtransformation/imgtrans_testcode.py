import os
import requests

import replicate
from PIL import Image
import io
from dotenv import load_dotenv

# env파일 같은 디렉토리에 둬야함
load_dotenv()

api_token = os.getenv('REPLICATE_API_TOKEN')
if not api_token:
    raise ValueError("REPLICATE_API_TOKEN is not set in environment variables")
client = replicate.Client(api_token=api_token)


local_image_path = 'C:\\Users\\user\\Desktop\\PR\\backend\\model_api_최종\\fastapi\\domain\\placerecognition\\utils\\buildingimg\\3600.jpg'  # 로컬 이미지 경로 설정
image = Image.open(local_image_path)
image = image.convert('RGB')

# Resize image to be at most 512x512 while maintaining aspect ratio
max_size = (512, 512)
image.thumbnail(max_size)

img_byte_arr = io.BytesIO()
image.save(img_byte_arr, format='JPEG')
img_byte_arr.seek(0)

output = client.run(
    "mcai/edge-of-realism-v2.0-img2img:0f7ba6926ca1e836e6dc64cf7e371402c9a4915851234378319f9b9b0f968fda",
    input={
        "image": img_byte_arr,
        "prompt": """Change the human face to animation characters.
        8K, 2D with 3D-like depth and shading,
        university campus atmosphere, game-like stylized
        art ABSOLUTELY CRITICAL: Preserve ALL original
        elements, structures, and compositions WITHOUT
        ANY ALTERATIONS
        Maintain exact shapes, sizes, and positions of all
        buildings, objects, and landscape features
        CRITICAL: Maintain exact number of people as in
        original image - if zero people in original, zero
        people in result; if N people in original, exactly N
        people in result
        If people are present: maintain their exact
        positions, poses, clothing colors and styles, hair
        color and style, facial expressions; transform into
        stylized characters while keeping individual
        distinctions
        If no people in original: focus on existing campus
        elements without adding any human figures or
        silhouettes
        Apply game-like art style ONLY to textures and
        colors, NOT to shapes or layouts
        DO NOT change, add, or remove any elements
        from the original image
        Emphasize existing university campus features
        while strictly maintaining original architecture and
        layout""",
        "upscale": 1,
        "strength": 0.5,
        "scheduler": "EulerAncestralDiscrete",
        "num_outputs": 1,
        "guidance_scale": 7.5,
        "negative_prompt": "disfigured, kitsch, ugly, oversaturated, greain, low-res, Deformed, blurry, bad anatomy, disfigured, poorly drawn face, mutation, mutated, extra limb, ugly, poorly drawn hands, missing limb, blurry, floating limbs, disconnected limbs, malformed hands, blur, out of focus, long neck, long body, ugly, disgusting, poorly drawn, childish, mutilated, mangled, old, surreal, calligraphy, sign, writing, watermark, text, body out of frame, extra legs, extra arms, extra feet, out of frame, poorly drawn feet, cross-eye, blurry, bad anatomy",
        "num_inference_steps": 25
    }
)

response = requests.get(output[0])

# URL에서 이미지를 로드
image = Image.open(io.BytesIO(response.content))

# 이미지 보여주기
image.show()
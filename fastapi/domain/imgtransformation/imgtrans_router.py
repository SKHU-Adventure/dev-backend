import os
from fastapi import File, UploadFile, APIRouter, Depends, HTTPException
import replicate
from PIL import Image
import io
from dotenv import load_dotenv

# env파일 같은 디렉토리에 둬야함
load_dotenv()

router = APIRouter(
    prefix="/model",
)

api_token = os.getenv('REPLICATE_API_TOKEN')
if not api_token:
    raise ValueError("REPLICATE_API_TOKEN is not set in environment variables")
client = replicate.Client(api_token=api_token)

@router.post("/transform/")
async def image_transfer(file: UploadFile = File(...)):
    try:
        image = Image.open(io.BytesIO(await file.read()))
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Error reading files: {e}")

    # Convert image to JPEG if not already in JPEG format
    if image.format != 'JPEG':
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
            "prompt": "Change the human face to animation characters.",
            "upscale": 1,
            "strength": 0.5,
            "scheduler": "EulerAncestralDiscrete",
            "num_outputs": 1,
            "guidance_scale": 7.5,
            "negative_prompt": "disfigured, kitsch, ugly, oversaturated, greain, low-res, Deformed, blurry, bad anatomy, disfigured, poorly drawn face, mutation, mutated, extra limb, ugly, poorly drawn hands, missing limb, blurry, floating limbs, disconnected limbs, malformed hands, blur, out of focus, long neck, long body, ugly, disgusting, poorly drawn, childish, mutilated, mangled, old, surreal, calligraphy, sign, writing, watermark, text, body out of frame, extra legs, extra arms, extra feet, out of frame, poorly drawn feet, cross-eye, blurry, bad anatomy",
            "num_inference_steps": 30
        }
    )
    return {"output": output}

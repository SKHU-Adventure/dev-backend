from fastapi import FastAPI, File, UploadFile
from starlette.middleware.cors import CORSMiddleware

from domain.placerecognition import pr_router
from domain.imgtransformation import imgtrans_router

app = FastAPI()

origins = [
    "http://127.0.0.1:5173",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(pr_router.router)
app.include_router(imgtrans_router.router)
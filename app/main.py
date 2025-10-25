# app/main.py
from fastapi import FastAPI
from app.config import settings

app = FastAPI(title="Meu Backend API", version="1.0")

@app.get("/")
def read_root():
    return {"message": "🚀 API está rodando!", "env": settings.ENV_NAME}

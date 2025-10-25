# app/config.py
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    ENV_NAME: str
    DATABASE_URL: str
    SECRET_KEY: str

    class Config:
        env_file = ".env"

settings = Settings()

FROM python:3.8-slim
ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
RUN apt-get update
RUN apt-get install -y libxml2-dev libxslt-dev python-lxml gcc
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.7.3/wait /wait
RUN chmod +x /wait
COPY . .
EXPOSE 5000

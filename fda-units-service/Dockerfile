FROM python:3.8-slim
MAINTAINER Cornelia Michlits <cornelia.michlits@tuwien.ac.at>

ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
ENV PORT_APP=5010
ENV FLASK_ENV=production

WORKDIR /app

COPY requirements.txt requirements.txt

RUN pip install -r requirements.txt > /dev/null

COPY ./us-yml/*.yml ./
COPY ./onto/*.ttl ./onto/
COPY ./*.py ./
COPY ./service_ready ./

RUN chmod +x ./service_ready

EXPOSE $PORT_APP
ENTRYPOINT [ "python", "./pywsgi.py" ]
CMD ["flask", "run"]

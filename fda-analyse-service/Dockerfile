FROM python:3.8-slim
MAINTAINER Cornelia Michlits <cornelia.michlits@tuwien.ac.at>

ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0

RUN apt-get update > /dev/null
RUN apt-get install -y libxml2-dev libxslt-dev python-lxml gcc > /dev/null

COPY requirements.txt requirements.txt

RUN pip install -r requirements.txt > /dev/null

# Add docker-compose-wait tool -------------------
ENV WAIT_VERSION 2.7.2

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait

RUN chmod +x /wait

COPY . .

EXPOSE 5000

# TODO on runtime displays: "WARNING: This is a development server. Do not use it in a production deployment."
CMD ["flask", "run"]
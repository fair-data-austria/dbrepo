#!/usr/bin/env python3
import pika
import os
from json import loads, dumps
from urllib.request import urlopen
from time import sleep


def get():
    url = 'https://www.wienerlinien.at/ogd_realtime/trafficInfoList?name=stoerunglang'
    response = loads(urlopen(url).read())
    return response['data']['trafficInfos']


def send():
    credentials = pika.PlainCredentials('fda', 'fda')
    parameters = pika.ConnectionParameters('fda-broker-service', 5672, '/', credentials)
    connection = pika.BlockingConnection(parameters=parameters)
    channel = connection.channel()
    for item in get():
        message = {
            'name': item['name'],
            'priority': item['priority'],
            'owner': item['owner'],
            'title': item['title'],
            'description': item['description'],
            'start_time': item['time']['start'],
            'end_time': item['time']['end']
        }
        payload = dumps(message).encode('utf8')
        print('payload', payload)
        channel.basic_publish(exchange='', routing_key=os.getenv('KEY'), body=payload)
    connection.close()


if __name__ == "__main__":
    if os.getenv('EXCHANGE') is None:
        exit(1)
    if os.getenv('KEY') is None:
        exit(2)
    print('Started AMQP producer.')
    while 1:
        send()
        sleep(100)

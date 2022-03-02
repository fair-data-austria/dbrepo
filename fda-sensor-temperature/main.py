#!/usr/bin/env python3
import pika
import os
import random
from json import dumps
from decimal import Decimal
from time import sleep


def rand():
    number = Decimal(random.randrange(140, 170))
    return float(number / 10)


def send():
    credentials = pika.PlainCredentials('fda', 'fda')
    parameters = pika.ConnectionParameters('fda-broker-service', 5672, '/', credentials)
    connection = pika.BlockingConnection(parameters=parameters)
    channel = connection.channel()
    message = {
        'sensor': os.getenv('NAME'),
        'value': rand()
    }
    payload = dumps(message).encode('utf8')
    print('payload', payload)
    channel.basic_publish(exchange='', routing_key=os.getenv('KEY'), body=payload)
    connection.close()


if __name__ == "__main__":
    if os.getenv('KEY') is None:
        exit(1)
    print('Started AMQP producer.')
    while 1:
        send()
        sleep(1000)

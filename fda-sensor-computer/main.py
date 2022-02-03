#!/usr/bin/env python3
import pika
from time import sleep


def get_wifi():
    f = open('/sys/class/thermal/thermal_zone0/temp', 'r')
    return f.read()


def send(ex, key, temp):
    connection = pika.BlockingConnection()
    channel = connection.channel()
    channel.basic_publish(exchange=ex, routing_key=key, body=bytes('{"wifi":' + str(temp) + '}', encoding='utf8'))
    connection.close()


if __name__ == "__main__":
    while 1:
        t = get_wifi()
        send('fda', 'fda.sensors.wifi', t)
        print('sent wifi temperature', t)
        sleep(1)

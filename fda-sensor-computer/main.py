#!/usr/bin/env python3
import pika
from time import sleep
from sys import argv


def get_wifi():
    f = open('/sys/class/thermal/thermal_zone0/temp', 'r')
    temp = f.read()
    return float(temp) / 1000.0


def send(temp):
    connection = pika.BlockingConnection()
    channel = connection.channel()
    ex = argv[1]
    key = argv[2]
    channel.basic_publish(exchange=ex, routing_key=key, body=bytes('{"temp":' + str(temp) + '}', encoding='utf8'))
    connection.close()


if __name__ == "__main__":
    if len(argv) != 3:
        print("USAGE: ./send EXCHANGE KEY")
        exit(1)
    while 1:
        t = get_wifi()
        send(t)
        print('sent wifi temperature', t)
        sleep(1)

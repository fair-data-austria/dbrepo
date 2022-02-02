#!/usr/bin/env python3
from coinbase.wallet.client import Client
from os import getenv
from time import sleep

print("Started.")

client = Client(getenv('API_KEY'), getenv('API_SECRET'))

price = client.get_sell_price(currency_pair='BTC-USD')
print("current price is %v", price)
sleep(5)

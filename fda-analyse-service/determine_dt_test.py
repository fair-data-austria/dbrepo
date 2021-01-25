# -*- coding: utf-8 -*-
"""
Created on Fri Sep 25 22:39:47 2020

@author: Co
"""
import messytables 

# int, float, boolean, date: dd-mm-yyyy, text
print('IN: int, float, boolean, date: dd-mm-yyyy, text') 
print(determine_datatypes('testdt01.csv'), '\n')

# int, float, boolean, time: hh:mm PM, text 
print('IN: int, float, boolean, time: hh:mm PM, text')
print(determine_datatypes('testdt02.csv'), '\n')

# int, float, boolean, date: dd.mm.yyyy hh:mm, text
print('IN: int, float, boolean, time: dd.mm.yyyy hh:mm, text')
print(determine_datatypes('testdt03.csv'), '\n')

# int, float, boolean, date: yyyy-mm-dd hh:mm, text 
print('IN: int, float, boolean, date: yyyy-mm-dd hh:mm, text')
print(determine_datatypes('testdt04.csv'), '\n')

# int, float, boolean, date: yyyy-mm-dd hh:mm:ss, text 
print('IN: int, float, boolean, date: yyyy-mm-dd hh:mm:ss, text')
print(determine_datatypes('testdt05.csv'), '\n')

# int, float, boolean, date: yyyy-mm-dd hh:mm:ss, text 
print('IN: int, float, boolean, date: yyyy/mm/dd, text')
print(determine_datatypes('testdt07.csv'), '\n')

# int, float, boolean, time: hh-mm-ss, text
print('int, float, boolean, time: hh-mm-ss, text') 
print(determine_datatypes('testdt08.csv'), '\n')

# Seperator: ";"

# huge file (approx 400 000 rows)
print('Huge file')
print(determine_datatypes('comments.csv'))

# TSV, 'any'

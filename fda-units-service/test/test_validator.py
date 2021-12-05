#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Dec  5 19:41:04 2021

@author: Cornelia Michlits
"""
import unittest 
import sys
sys.path.append("..")
from validate import validator, stringmapper

exp_out_True = "{'valid': True}"
exp_out_False = "{'valid': False}"

class TestStringMethods(unittest.TestCase):

    #metre is SI Unit
    def test_validator_true(self): 
        self.assertEqual(str(validator('metre')),exp_out_True)
    #diameter is measure, but no SI Unit
    def test_validator_no_SI_Unit(self): 
        self.assertEqual(str(validator('diameter')),exp_out_False)
    #misspelling
    def test_validator_misspelling(self): 
        self.assertEqual(str(validator('metreee')),exp_out_False)
    #Divided unit
    def test_validator_dividedunit(self): 
        self.assertEqual(str(validator(stringmapper('mole per metre'))),exp_out_True)
    #Prefixed unit
    def test_validator_prefixedunit(self): 
        self.assertEqual(str(validator(stringmapper('zettamole'))),exp_out_True)

if __name__ == '__main__':
    unittest.main()
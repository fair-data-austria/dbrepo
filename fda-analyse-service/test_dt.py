import unittest 
from determine_dt import determine_datatypes

exp_out_enumTrue = '{"columns": {"int": "Integer", "float": "Numeric", "string": "Text", "boolean": "Boolean", "date": "Date", "time": "Timestamp", "enum": {"Enum": ["em", "zf", "ac", "mk"]}}}'
exp_out_enumFalse = '{"columns": {"int": "Integer", "float": "Numeric", "string": "Text", "boolean": "Boolean", "date": "Date", "time": "Timestamp", "enum": "Text"}}'

class TestStringMethods(unittest.TestCase):

    def test_dt_enumTrue(self): 
        self.assertEqual(determine_datatypes('data/test_dt/test_dt.csv',enum=True,enum_tol=0.1,seperator=','),exp_out_enumTrue)
    def test_dt_enumFalse(self): 
        self.assertEqual(determine_datatypes('data/test_dt/test_dt.csv',seperator=','),exp_out_enumFalse)
        
if __name__ == '__main__':
    unittest.main()
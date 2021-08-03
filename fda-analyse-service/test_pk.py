import unittest 
from determine_pk import determine_pk 

class TestStringMethods(unittest.TestCase):

    def test_largefile_idfirst(self): 
        self.assertTrue('"id": 1' in determine_pk('data/test_pk/test_largefile_idfirst.csv'))
        
    def test_largefile_idinbtw(self):
        self.assertTrue('"id": 1' in determine_pk('data/test_pk/test_largefile_idinbtw.csv'))
    
    def test_largefile_no_pk(self): 
        self.assertEquals(determine_pk('data/test_pk/test_largefile_no_pk.csv'),'{}')
        
    def test_largefile_nullinunique(self): 
        self.assertFalse('uniquestr' in determine_pk('data/test_pk/test_largefile_nullinunique.csv'))
        
    def test_smallfile_idfirst(self): 
        self.assertTrue('"id": 1' in determine_pk('data/test_pk/test_smallfile_idfirst.csv'))
        
    def test_smallfile_idinbtw(self):
        self.assertTrue('"id": 1' in determine_pk('data/test_pk/test_smallfile_idinbtw.csv'))
        
    def test_smallfile_no_pk(self): 
        self.assertEquals(determine_pk('data/test_pk/test_smallfile_no_pk.csv'),'{}')
        
    def test_smallfile_nullinunique(self): 
        self.assertFalse('uniquestr' in determine_pk('data/test_pk/test_smallfile_nullinunique.csv'))
        
#    def test_smallfile_idnotunique(self): 
#        self.assertFalse('id' in determine_pk('data/test_pk/test_smallfile_idnotunique.csv'))
        
if __name__ == '__main__':
    unittest.main()
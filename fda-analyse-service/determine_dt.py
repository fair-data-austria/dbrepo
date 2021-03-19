# -*- coding: utf-8 -*-
"""
Created on Fri Sep 25 21:25:09 2020
From: 
https://messytables.readthedocs.io/en/latest/

https://github.com/okfn/messytables/

@author: Co
"""

import json  
import messytables
from messytables import CSVTableSet, type_guess, \
   headers_guess, headers_processor, offset_processor
  
  
def determine_datatypes(path, enum=False, enum_tol=0.001):
# Use option enum=True for searching Postgres ENUM Types in CSV file     
    fh = open(path, 'rb')
    
    # Load a file object:
    table_set = CSVTableSet(fh)
    
    # A table set is a collection of tables:
    row_set = table_set.tables[0]
       
    # guess header names and the offset of the header:
    offset, headers = headers_guess(row_set.sample)
    row_set.register_processor(headers_processor(headers))
        
    # add one to begin with content, not the header:
    row_set.register_processor(offset_processor(offset + 1))
    
    # guess column types:
    types = type_guess(row_set.sample, strict=True)
        
    r = {}
    
    n=0
    for i in row_set: 
       n=n+1
    
    for i in range(0,(len(types))):
        if type(types[i]) == messytables.types.BoolType: 
            r[headers[i]] = "Boolean"
        elif type(types[i]) == messytables.types.IntegerType:
            r[headers[i]] = "Integer" 
        elif type(types[i]) == messytables.types.DateType: 
            if ("S" in str(types[i])): 
                r[headers[i]] = "Timestamp"
            else: 
                r[headers[i]] = "Date"
        elif type(types[i]) == messytables.types.DecimalType: 
            r[headers[i]] = "Numeric"
        else : 
            if enum == True: 
                enum_set = set()
                m=0
                for elem in row_set: 
                    if m/n < enum_tol: 
                        enum_set.add(elem[i].value)
                        r[headers[i]] = {"Enum": list(enum_set-{headers[i]})}
                    else: 
                        r[headers[i]] = "String"
                        break 
                    m = len(enum_set)
            else: 
                r[headers[i]] = "String"
    
    s ={ 'columns' : r } 
                
    return json.dumps(s)

""" 
{
  "columns": {
    "additionalProp1": "string",
    "additionalProp2": "string",
    "additionalProp3": "string"
  },
  "primaryKey": "string",
  "tableName": "string"
}
""" 
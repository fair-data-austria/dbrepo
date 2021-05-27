# -*- coding: utf-8 -*-
"""
Created on Fri Sep 25 21:25:09 2020
From: 
https://messytables.readthedocs.io/en/latest/

https://github.com/okfn/messytables/

@author: Co
"""

import json  
import messytables, pandas as pd
from messytables import CSVTableSet, type_guess, \
   headers_guess, headers_processor, offset_processor
  
  
def determine_datatypes(path, enum=False, enum_tol=0.0001,seperator=','):
# Use option enum=True for searching Postgres ENUM Types in CSV file. Remark 
# Enum is not SQL standard, hence, it might not be supported by all db-engines. 
# However, it can be used in Postgres and MySQL. 
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

    # list of rows 
    if enum ==True: 
        rows = pd.read_csv(path,sep=seperator,header=offset)
        n = len(rows)
    
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
                is_enum = True
                for elem in range(0,n): 
                    if (m < enum_tol*n): 
                        enum_set.add(rows.iloc[elem,i])
                    else: 
                        is_enum = False
                        break
                    m = len(enum_set)
                if is_enum:
                    enum_set.discard(None)
                    r[headers[i]] = {"Enum": list(enum_set)}   
                else: 
                    r[headers[i]] = "Text"
            else: 
                r[headers[i]] = "Text"
    
    s ={ 'columns' : r } 
                
    return json.dumps(s)

""" 
Example output:
{
  "columns": {
    "col1": "integer",
    "col2": "string",
    "col3": "string"
  }
}
""" 

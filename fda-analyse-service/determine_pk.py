import json 
import pandas as pd
import random 
from pathlib import Path
import numpy as np
from determine_dt import determine_datatypes

def determine_pk(filepath,seperator=','): 
    dt = json.loads(determine_datatypes(filepath,seperator)) 
    dt = {k.lower(): v for k, v in dt['columns'].items()}
    # {k.lower(): v for k, v in dt['columns'].items() if v != 'Numeric'}
    colnames = dt.keys()
    colindex = list(range(0,len(colnames)))
    if Path(filepath).stat().st_size < 3000: # precise if lower than 400kB     
        pk = {}
        j = 0
        k = 0
        
        for item in colnames: 
            if item == 'id': 
                j=j+1
                pk.update({item:j})
                colindex.remove(k)
            k=k+1
                
        csvdata = pd.read_csv(filepath,sep=seperator) 
            
        for i in colindex: 
            if pd.Series(csvdata.iloc[:,i]).is_unique and pd.Series(csvdata.iloc[:,i]).notnull().values.any(): 
                j=j+1
                pk.update({list(colnames)[i]:j})
    else: # stochastic pk determination 
        pk = {}
        j = 0
        k = 0
        
        for item in colnames: 
            if item == 'id': 
                j=j+1
                pk.update({item:j})
                colindex.remove(k)
            k=k+1
            
        p = get_sampling_percentage(filepath)
        
        csvdata = pd.read_csv(
         filepath,
         sep=seperator,
         header=0, 
         skiprows=lambda i: i>0 and random.random() > p)
            
        for i in colindex: 
            if pd.Series(csvdata.iloc[:,i]).is_unique and pd.Series(csvdata.iloc[:,i]).notnull().values.any(): 
                j=j+1
                pk.update({list(colnames)[i]:j})
        
    return json.dumps(pk)

def get_sampling_percentage(filepath): 
    sz = Path(filepath).stat().st_size
    p = np.log10(sz) # logarithmic scaled percentage of random inspected rows 
    return p 

# =============================================================================

""" 
Example output with priority ranking:
{
  "primary key": {
    "cola": 3,
    "colb": 1,
    "colc": 2
  }
}
""" 
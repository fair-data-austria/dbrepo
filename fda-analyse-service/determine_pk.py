import json 
import pandas as pd
from pathlib import Path
from determine_dt import determine_datatypes

# todo: depending on filesize stochatic method: Path(filepath).stat().st_size
def determine_pk(path,seperator=','): 
    dt = json.loads(determine_datatypes(path,seperator)) 
    dt = {k.lower(): v for k, v in dt['columns'].items()}
    # {k.lower(): v for k, v in dt['columns'].items() if v != 'Numeric'}
    colnames = dt.keys()
    colindex = list(range(0,len(colnames)))
    
    pk = {}
    j = 0
    k = 0
    
    for item in colnames: 
        if item == 'id': 
            j=j+1
            pk.update({item:j})
            colindex.remove(k)
        k=k+1
            
    csvdata = pd.read_csv(path,sep=seperator) 
        
    for i in colindex: 
        if pd.Series(csvdata.iloc[:,i]).is_unique and pd.Series(csvdata.iloc[:,i]).notnull().values.any(): 
            j=j+1
            pk.update({list(colnames)[i]:j})
    
    return pk

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
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Dec  4 11:37:19 2021

@author: Cornelia Michlits
"""

import rdflib
import re
g = rdflib.Graph()
g.namespace_manager.bind('om', 'http://www.ontology-of-units-of-measure.org/resource/om-2/')
g.parse('onto/om-2.ttl', format='turtle')

om = rdflib.Namespace('http://www.ontology-of-units-of-measure.org/resource/om-2/')
rdf_schema = rdflib.Namespace('http://www.w3.org/2000/01/rdf-schema#')

def list_units(string):
    if bool(re.match('^[a-zA-Z0-9]+$',string)): 
        l_query = """
        SELECT ?symbol ?name ?comment
        WHERE {
            ?unit om:symbol ?symbol .
            ?unit <http://www.w3.org/2000/01/rdf-schema#label> ?name .
            ?unit <http://www.w3.org/2000/01/rdf-schema#comment> ?comment .
            FILTER regex(str(?unit),\""""+string+"""\","i")
            } LIMIT 10 """
        qres = g.query(l_query)
        for row in qres: 
            print(f"{row.symbol} | {row.name} | {row.comment}")
    else: 
        return 'not alphanumeric'
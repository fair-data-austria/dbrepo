#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Dec  2 23:31:39 2021

@author: Cornelia Michlits
"""
import rdflib
g = rdflib.Graph()
g.namespace_manager.bind('om', 'http://www.ontology-of-units-of-measure.org/resource/om-2/')
g.parse('onto/om-2.ttl', format='turtle')

om = rdflib.Namespace('http://www.ontology-of-units-of-measure.org/resource/om-2/')
_exhausted = object()
def validator(value):
    #input str
    tmp = str(om)+value
    t_uri = rdflib.term.URIRef(tmp)
    if next(g.triples((t_uri,None,om.Unit)), _exhausted) is _exhausted and next(g.triples((t_uri,None,om.PrefixedUnit)),_exhausted) is _exhausted and next(g.triples((t_uri,None,om.UnitDivision)),_exhausted) is _exhausted:
        return {"valid": False}
    else:
        return {"valid": True}
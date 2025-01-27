package com.dke.ir.p05SearchEngine;

import java.io.*;
import java.util.*;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public class VectorGenerator {
	DocVector[] docVector;
    private Map<String,Integer> allterms;
    Integer totalNoOfDocumentInIndex;
    IndexReader indexReader;
    
    public VectorGenerator() throws IOException {
        allterms = new HashMap<>();
        indexReader = IndexOpener.GetIndexReader();
        totalNoOfDocumentInIndex = IndexOpener.TotalDocumentInIndex();
        docVector = new DocVector[totalNoOfDocumentInIndex];
    }
    
    public void GetAllTerms() throws IOException {
        AllTerms allTerms = new AllTerms();
        allTerms.initAllTerms();
        allterms = allTerms.getAllTerms();
    }
    
    public DocVector[] GetDocumentVectors() throws IOException {
        for (int docId = 0; docId < totalNoOfDocumentInIndex; docId++) {
            Terms vector = indexReader.getTermVector(docId, "contents");
            TermsEnum termsEnum = null;
            termsEnum = vector.iterator();
            BytesRef text = null;            
            docVector[docId] = new DocVector(allterms);            
            while ((text = termsEnum.next()) != null) {
                String term = text.utf8ToString();
                int freq = (int) termsEnum.totalTermFreq();
                docVector[docId].setEntry(term, freq);
            }
            docVector[docId].normalize();
        }        
        indexReader.close();
        return docVector;
    }
}

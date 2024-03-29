package com.alag.ci.blog.classify.weka.impl;

import java.util.ArrayList;
import java.util.Enumeration;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import peopleclassifier.PageTextDataSetCreatorImpl;
import peopleclassifier.TextDataItem;
import peopleclassifier.WEKAPredictiveBlogDataSetCreatorImpl;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class WEKABlogClassifier {

    public enum Algorithm {

        DECISION_TREE, NAIVE_BAYES, BAYES_NET,
        LINEAR_REGRESSION, MLP, RBF
    };
    private boolean isContinuous = false;
    Classifier classifier = null;
    Instances instances = null;
    private String trainingDir = "";
    WEKAPredictiveBlogDataSetCreatorImpl dataSetCreator = null;

    public WEKABlogClassifier(String trainingDir) {
        this.trainingDir = trainingDir;
    }

    public void classify(Algorithm algorithm) throws Exception {
        instances = createLearningDataset(trainingDir);
        classifier = getClassifier(instances, algorithm);
        evaluateModel(instances, classifier);

//        predictUnknownCases(instances, classifier);



        // todo - weka tutorial is as below
        // need to do something similar
        // that is find all attributes but have a learning data set and an 
        // predict dataset
        // a;so need some way of tieing it back to the original data item
        // for first pass create instances from learning data set and 
        // print out actual as against prediction
//        FastVector allAttributes = createAttributes();
//        Instances learningDataset = createLearningDataSet(allAttributes);
//        Classifier predictiveModel = learnPredictiveModel( learningDataset);
//        Evaluation evaluation = evaluatePredictiveModel(predictiveModel,  learningDataset);
//        System.out.println(evaluation.toSummaryString());
//        predictUnknownCases(learningDataset,predictiveModel);
        J48 theClassifier = null;

        try {
            theClassifier = (J48) classifier;
        } catch (ClassCastException theEx) {
        }

        if (theClassifier != null) {
            System.out.println(theClassifier.graph());
        }
        //     plotData(learningDataset, predictiveModel);
    }

    public List<String> classifyAll(String fullDataDir) {
        List<String> retVal = new ArrayList<String>();
        PageTextDataSetCreatorImpl pt = new PageTextDataSetCreatorImpl(fullDataDir, null);
        List<TextDataItem> testData = new ArrayList<TextDataItem>();

        try {
            testData = pt.createLearningData();
            WEKAPredictiveBlogDataSetCreatorImpl.setPersonAttribute(testData);
        } catch (Exception ex) {
            Logger.getLogger(WEKABlogClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (TextDataItem theItem : testData) {
            String theURL = theItem.getData().getUrl();
            double isPerson = 1.0;

            if (theItem.isPerson()) {
                isPerson = 0.0;
            }

            Instance theInstance = dataSetCreator.reCreateInstance(instances, theItem, isContinuous);
            try {
                double classification = classifier.classifyInstance(theInstance);
                boolean bClassification = false;
                if(classification == 0.0){
                    retVal.add(theURL);
                    bClassification = true;
                }

                if (classification == isPerson) {
                    System.out.println("URL: " + theURL + " is person = " + Boolean.toString(theItem.isPerson()) + " marked correctly as : " + Boolean.toString(bClassification));
                } else {
                    System.out.println("URL: " + theURL + " is person = " + Boolean.toString(theItem.isPerson()) + " marked INCORRECTLY as : " + Boolean.toString(bClassification));
                }
            } catch (Exception ex) {
                Logger.getLogger(WEKABlogClassifier.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        return retVal;
    }

    protected Instances createLearningDataset(String trainingDataDir) throws Exception {
        dataSetCreator =
                new WEKAPredictiveBlogDataSetCreatorImpl(trainingDataDir, null);
        return dataSetCreator.createLearningDataSet("LearningData", isContinuous);
    }

    protected void evaluateModel(Instances instances, Classifier classifier)
            throws Exception {
        Evaluation modelEval = new Evaluation(instances);
        modelEval.evaluateModel(classifier, instances);
        System.out.println(modelEval.toSummaryString("\nResults\n", true));

        for (Enumeration e = instances.enumerateInstances(); e.hasMoreElements();) {
            printInstancePrediction((Instance) e.nextElement(), classifier);
        }
    }

    protected void printInstancePrediction(Instance instance,
            Classifier classifier) throws Exception {
        double classification = classifier.classifyInstance(instance);
        System.out.println("Classification = " + classification);
    }

    protected Classifier getClassifier(Instances instances,
            Algorithm algorithm) throws Exception {
        classifier = getClassifier(algorithm);
        //NOTE - last attribute is used for classification
        instances.setClassIndex(instances.numAttributes() - 1);
        classifier.buildClassifier(instances);
        return classifier;
    }

    protected Classifier getClassifier(Algorithm algorithm) throws Exception {
        classifier = null;
        if (Algorithm.DECISION_TREE.equals(algorithm)) {
            classifier = new J48();
        } else if (Algorithm.NAIVE_BAYES.equals(algorithm)) {
            classifier = new NaiveBayes();
        } else if (Algorithm.BAYES_NET.equals(algorithm)) {
            classifier = new BayesNet();
        }
        System.out.println(classifier.getCapabilities());
        return classifier;
    }

    private void predictUnknownCases(Instances learningDataset, Classifier predictiveModel)
            throws Exception {
//        Instance testMaleInstance = 
//            createInstance(learningDataset,32., "male", 0) ;
////        Instance testFemaleInstance = 
////            createInstance(learningDataset,32., "female", 0) ;
//        double malePrediction = 
//            predictiveModel.classifyInstance(testMaleInstance);
////        double femalePrediction = 
////            predictiveModel.classifyInstance(testFemaleInstance);
//        System.out.println("Predicted number of logins [age=32]: ");
//        System.out.println("\tMale = " + malePrediction);
//        System.out.println("\tFemale = " + femalePrediction);
    }
}

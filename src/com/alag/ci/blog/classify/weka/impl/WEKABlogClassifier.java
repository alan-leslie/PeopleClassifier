package com.alag.ci.blog.classify.weka.impl;

import java.util.Enumeration;

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

//import com.alag.ci.blog.dataset.impl.WEKAPredictiveBlogDataSetCreatorImpl;

public class WEKABlogClassifier {
    public enum Algorithm  {DECISION_TREE, NAIVE_BAYES, BAYES_NET,
        LINEAR_REGRESSION, MLP, RBF};
    
    WEKAPredictiveBlogDataSetCreatorImpl dataSetCreator = null;
    
    public void classify(Algorithm algorithm) throws Exception {
        Instances instances = createLearningDataset();
        Classifier classifier = getClassifier(instances,algorithm);
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
   //     plotData(learningDataset, predictiveModel);
    }
    
    protected Instances createLearningDataset() throws Exception {
        dataSetCreator = 
            new WEKAPredictiveBlogDataSetCreatorImpl("/home/al/wiki_scots/crawl-small/processed/", null);
        return dataSetCreator.createLearningDataSet("LearningData", false);
    }
    
    protected void evaluateModel(Instances instances, Classifier classifier) 
        throws Exception {
        Evaluation modelEval = new Evaluation(instances);
        modelEval.evaluateModel(classifier, instances);
        System.out.println(modelEval.toSummaryString("\nResults\n", true));
       // System.out.println(((J48)classifier).graph());
        int i = 0;
        for (Enumeration e = instances.enumerateInstances() ; e.hasMoreElements() ;) {
            printInstancePrediction((Instance)e.nextElement(),classifier, i);
            ++i;
        }
    }
    
    protected void printInstancePrediction(Instance instance, 
            Classifier classifier,
            int index) throws Exception {
        double classification = classifier.classifyInstance(instance);
//        TextDataItem theItem = dataSetCreator.getItemAt(index);
//        if(theItem != null){
//            System.out.println("Item: " + theItem.getData().getUrl() + " is person = " + theItem.isPerson());
//        }
        System.out.println("Classification = " + classification);
    }
 
    protected Classifier getClassifier(Instances instances,
         Algorithm algorithm) throws Exception {
        Classifier classifier = getClassifier(algorithm);
        //last attribute is used for classification
        instances.setClassIndex(instances.numAttributes() - 1);
        classifier.buildClassifier(instances);
        return classifier;
    }
    
    protected Classifier getClassifier(Algorithm algorithm) throws Exception {
           Classifier classifier = null;
           if (Algorithm.DECISION_TREE.equals(algorithm)) {
               classifier = new J48();
           } else if (Algorithm.NAIVE_BAYES.equals(algorithm)) {
               classifier = new NaiveBayes() ;
           } else if (Algorithm.BAYES_NET.equals(algorithm)) {
               classifier = new BayesNet() ;
           } 
           System.out.println(classifier.getCapabilities());
           return classifier;
    }
    
    private void predictUnknownCases(Instances learningDataset, Classifier predictiveModel) 
        throws Exception {
        Instance testMaleInstance = 
            createInstance(learningDataset,32., "male", 0) ;
//        Instance testFemaleInstance = 
//            createInstance(learningDataset,32., "female", 0) ;
        double malePrediction = 
            predictiveModel.classifyInstance(testMaleInstance);
//        double femalePrediction = 
//            predictiveModel.classifyInstance(testFemaleInstance);
        System.out.println("Predicted number of logins [age=32]: ");
        System.out.println("\tMale = " + malePrediction);
//        System.out.println("\tFemale = " + femalePrediction);
    }
           
    private Instance createInstance(Instances associatedDataSet,
            double age, String gender, int numLogins) {
        // Create empty instance with three attribute values
        Instance instance = new SparseInstance(3);
        instance.setDataset(associatedDataSet);
        instance.setValue(0, age);
        instance.setValue(1, gender);
        instance.setValue(2, numLogins);
        return instance;
    }
    
    public static void main(String [] args) throws Exception {
        WEKABlogClassifier c = new WEKABlogClassifier();
        c.classify(Algorithm.DECISION_TREE);
    }

}

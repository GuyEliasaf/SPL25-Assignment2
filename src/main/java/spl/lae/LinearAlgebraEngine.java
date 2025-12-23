package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.LinkedList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        this.executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        
        try{
            while(true){
                if(computationRoot.getNodeType() == ComputationNodeType.MATRIX){
                    return computationRoot;
                }
                computationRoot.associativeNesting();
                ComputationNode temp = computationRoot.findResolvable();
                loadAndCompute(temp);
                temp.resolve(leftMatrix.readRowMajor());
            }
    }
        catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
        finally {
            try {
                executor.shutdown();
            }
            catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
            
        }
        }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        if(node == null) throw new IllegalArgumentException("node is null");
        ComputationNodeType type = node.getNodeType();
        if(type == ComputationNodeType.ADD || (type == ComputationNodeType.MULTIPLY)) {
            leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());
            rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
        }

        else if(type == ComputationNodeType.NEGATE) {
            leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());
        }

        else {
            leftMatrix.loadColumnMajor(node.getChildren().get(0).getMatrix());
        }

        if(type == ComputationNodeType.ADD) {
            if(leftMatrix.length() != rightMatrix.length()) throw new IllegalArgumentException("The matrices have different length");
            List<Runnable> task = createAddTasks();
            executor.submitAll(task);
        }

        if(type == ComputationNodeType.MULTIPLY) {
            if(leftMatrix.get(0).length() != rightMatrix.length()) throw new IllegalArgumentException("The left matrix number of columns is not equal to the right matrix number of rows");
            executor.submitAll(createMultiplyTasks());
        }

        if(type == ComputationNodeType.NEGATE) {
            executor.submitAll(createNegateTasks());
        }

        if(type == ComputationNodeType.TRANSPOSE) {
            executor.submitAll(createTransposeTasks());
        }
        
    }

    public List<Runnable> createAddTasks() {
        List<Runnable> ret = new LinkedList<>();
        int length = rightMatrix.length();
        
        for(int i = 0; i <length; i++){
            final int row = i; // Local variable i is required to be final
            Runnable task = () -> {
                try {
                    leftMatrix.get(row).add(rightMatrix.get(row));
                    ;
                    
                } 
                catch(Exception e) {
                    throw new IllegalArgumentException(e);
                }
            
            };
            ret.add(task);
        }
        return ret;
    }

    public List<Runnable> createMultiplyTasks() {
        List<Runnable> ret = new LinkedList<>();
        int length = leftMatrix.length();
        
        for(int i = 0; i <length; i++){
            final int row = i; // Local variable i is required to be final
            Runnable task = () -> {
                try {
                    leftMatrix.get(row).vecMatMul(rightMatrix);
                } 
                catch(Exception e) {
                    throw new IllegalArgumentException(e);
                }
            
            };
            ret.add(task);
        }
        return ret;
    }

    public List<Runnable> createNegateTasks() {
        List<Runnable> ret = new LinkedList<>();
        int length = leftMatrix.length();
        
        for(int i = 0; i <length; i++){
            final int row = i; // Local variable i is required to be final
            Runnable task = () -> {
                try {
                    leftMatrix.get(row).negate();
                    
                } 
                catch(Exception e) {
                    throw new IllegalArgumentException(e);
                }
            
            };
            ret.add(task);
        }
        return ret;
    }

    public List<Runnable> createTransposeTasks() {
        List<Runnable> ret = new LinkedList<>();
        int length = leftMatrix.length();
        
        for(int i = 0; i <length; i++){
            final int row = i; // Local variable i is required to be final
            Runnable task = () -> {
                try {
                    leftMatrix.get(row).transpose();
                } 
                catch(Exception e) {
                    throw new IllegalArgumentException(e);
                }
            
            };
            ret.add(task);
        }
        return ret;
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }
}

package org.example.Utils;
//TODO remove un-used imports
import java.util.Arrays;

public class Matrix {
    private int rows;
    private int columns;
    private int[][] elements;

    public Matrix(int[][] elements) {
        validateElements(elements);
        this.elements = elements; // TODO get copy of element to avoid update over origin matrix, same thing when getElements()
        this.rows = elements.length;
        this.columns = elements[0].length;
    }

    public int getVal(int row, int col){
       return elements[row][col];
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int[][] getElements() {
        return elements;
    }

    public Matrix matSummation(Matrix mat2){
        sameSize(mat2);
        int[][] matNew = new int[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.columns; j++){
                matNew[i][j] = this.getVal(i,j) + mat2.getVal(i,j);
            }
        }
        return new Matrix(matNew);
    }

    public Matrix scalarMultiplication(int scalar){
        int[][] matNew = new int[this.rows][this.columns];
        for (int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.columns; j++){
                matNew[i][j] = scalar*this.getVal(i,j);
            }
        }
        return new Matrix(matNew);
    }

    public int[][] dotProduct(Matrix mat2) throws Exception {
        checkMultiplySizes(this.columns, mat2.getRows());
        int[][] mux = new int[this.rows][mat2.getColumns()];

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < mat2.getColumns(); j++) {
                int tot = 0;
                for (int k = 0; k < mat2.getRows(); k++) {
                    tot += this.getVal(i, k) * mat2.getVal(k, j);
                }
                mux[i][j] = tot;
            }
        }
        return mux;
    }
    public Matrix transposeMat(){
        int[][] transpose = new int[this.columns][this.rows];

        for (int i = 0; i < this.rows; i++){
            for (int j = 0; j < this.columns; j++) {
                transpose[j][i] = this.getVal(i,j);
            }
        }
        return new Matrix(transpose);
    }

    public Matrix squareMatOps(SqaureOpsTypes needed) throws Exception {
        if(!this.checkSquare()) {
            throw new Exception("Cannot perform square matrix operations as Matrix is not square");
        }
        SquareOps squareOps = getOperationInstanceSqrMat(needed);
        return squareOps.doOperation(this.getElements());
    }

    private static SquareOps getOperationInstanceSqrMat(SqaureOpsTypes needed) {
        switch (needed) {
            case UPPER -> {
                return new Upper();
            }
            case LOWER -> {
                return new Lower();
            }
            default -> {
                return new Diagonal();
            }
        }
    }

    public Matrix subMatrix(int numOfRowsToCancel, int[] indexesToCancel) throws Exception {
        int numOfColumnsToCancel = indexesToCancel.length - numOfRowsToCancel;
        int[] rowToCancel = new int[numOfRowsToCancel];
        int[] columnToCancel = new int[numOfColumnsToCancel];

        rowsAndColsToCancel(indexesToCancel, rowToCancel, columnToCancel);
        exceptionsSubmatrix(rowToCancel, columnToCancel);

        int newRow = this.getRows() - numOfRowsToCancel;
        int newColumn = this.getColumns() - numOfColumnsToCancel;

        int[][] subMat = new int[newRow][newColumn];
        int rowInd = 0;
        for(int i = 0; i < this.getRows(); i++){
            if(!contains(rowToCancel, i)){
                int colInd = 0;
                for(int j = 0; j < this.getColumns(); j++){
                    if(!contains(columnToCancel,j)){
                        subMat[rowInd][colInd] = this.getVal(i,j);
                        colInd++;
                    }
                }
                rowInd++;
            }
        }
        return new Matrix(subMat);
    }

    public int determinant() throws Exception {
        if (!this.checkSquare()) {
            throw new Exception("Matrix is not square, so cannot calculate determinant.");
        }
        int det = 0;
        if (this.getRows() == 2) {
            det += (this.getVal(0,0) * this.getVal(1,1)) - (this.getVal(0,1) * this.getVal(1,0));
        } else {
            for (int i = 0; i < this.getRows(); i++) {
                Matrix sub = this.subMatrix(1,new int[] {0,i});
                int coef = this.getVal(0,i);
                det += (int) ((coef * Math.pow(-1, i)) * sub.determinant());
            }
        }
        return det;
    }

    public boolean checkSquare(){
        return this.getRows() == this.getColumns();
    }

    private void validateElements(int[][] elements) {
        if(elements == null){
            throw new IllegalArgumentException();
        }
        validateRows(elements);
        checkColumns(elements);
    }

    private void validateRows(int[][] elements) {
        for (int[] element : elements) {
            if (element == null) {
                throw new IllegalArgumentException("Cannot have null value as a row in the matrix");
            }
        }
    }

    private void checkColumns(int[][] elements) {
        for(int i = 0; i < elements.length; i++){
            if(elements[i].length != elements[0].length){
                throw new IllegalArgumentException("Check number of columns in all rows of the matrix is the same.");
            }
        }
    }

    private void sameSize(Matrix mat2){
        if(this.rows != mat2.getRows() || this.columns != mat2.getColumns()){
            throw new IllegalArgumentException("Matrix sizes do not match, so cannot perform matrix addition.");
        }
    }

    private void checkMultiplySizes(int cols, int rows) throws Exception {
        if (cols != rows) {
            throw new Exception("Cannot perform matrix multiplication because the number of columns of the left hand matrix does not equal the number of rows of the right hand matrix");
        }
    }

    private void rowsAndColsToCancel(int[] toCancel, int[] rowToCancel, int[] columnToCancel) {
        for (int i = 0, counter = 1, colInd = 0; i < toCancel.length; i++, counter++) {
            if (counter <= rowToCancel.length) {
                rowToCancel[i] = toCancel[i];
            } else {
                columnToCancel[colInd] = toCancel[i];
                colInd++;
            }
        }
    }

    private void exceptionsSubmatrix(int[] rowToCancel, int[] columnToCancel) throws Exception {
        checkHowMuchDeleted(rowToCancel.length, columnToCancel.length);
        checkRowOrColumnExceedMax(rowToCancel, columnToCancel);
    }

    private void checkHowMuchDeleted(int numOfRows, int numOfColumns) {
        if(this.getRows() == numOfRows || this.getColumns() == numOfColumns){
            throw new IllegalArgumentException("Deleted the whole matrix");
        }
    }

    private void checkRowOrColumnExceedMax(int[] rowToCancel, int[] columnToCancel) throws Exception {
        int maxRow = -1;
        int maxCol = -1;
        for(int i : rowToCancel){
            if(maxRow < i){
                maxRow = i;
            }
        }
        for(int i : columnToCancel){
            if(maxCol < i){
                maxCol = i;
            }
        }
        if (maxRow+1 > this.getRows()){
            throw new Exception("Cannot delete row as the index exceeds the number of rows in the Matrix");
        }
        if (maxCol+1 > this.getColumns()){
            throw new Exception("Cannot delete column as the index exceeds the number of columns in the Matrix");
        }
    }

    private boolean contains(int[] arr, int key){
        for (int i : arr){
            if (i == key){
                return true;
            }
        }
        return false;
    }

}

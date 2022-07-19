package com.neural.graphics.mnistReader;

import com.neural.graphics.BuildConfig;

import java.io.*;

public class MnistDataReader  {

    public float progress=0f;
    public MnistMatrix[]readData(InputStream dataFileStream,InputStream labelFileStream) throws IOException{
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(dataFileStream));
        int magicNumber = dataInputStream.readInt();
        int numberOfItems = dataInputStream.readInt();
        int nRows = dataInputStream.readInt();
        int nCols = dataInputStream.readInt();

        System.out.println("magic number is " + magicNumber);
        System.out.println("number of items is " + numberOfItems);
        System.out.println("number of rows is: " + nRows);
        System.out.println("number of cols is: " + nCols);

        DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(labelFileStream));
        int labelMagicNumber = labelInputStream.readInt();
        int numberOfLabels = labelInputStream.readInt();

        System.out.println("labels magic number is: " + labelMagicNumber);
        System.out.println("number of labels is: " + numberOfLabels);

        MnistMatrix[] data = new MnistMatrix[numberOfItems];

        if (BuildConfig.DEBUG && numberOfItems != numberOfLabels) {
            throw new AssertionError("Assertion failed");
        }


        for (int i = 0; i < numberOfItems; i++) {
            MnistMatrix mnistMatrix = new MnistMatrix(nRows, nCols);
            mnistMatrix.setLabel(labelInputStream.readUnsignedByte());
            for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nCols; c++) {
                    mnistMatrix.setValue(r, c, dataInputStream.readUnsignedByte());
                }
            }
            progress=((float)(i+1))/numberOfItems*100f;
            data[i] = mnistMatrix;
        }
        dataInputStream.close();
        labelInputStream.close();
        return data;
    }

    public MnistMatrix[] readData(String dataFilePath, String labelFilePath) throws IOException {
        return readData(new FileInputStream(dataFilePath), new FileInputStream(labelFilePath));
    }
}
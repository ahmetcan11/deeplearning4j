/*
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

package org.datavec.hadoop.records.reader.mapfile;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.datavec.hadoop.records.reader.mapfile.index.LongIndexToKey;
import org.datavec.hadoop.records.reader.mapfile.record.RecordCreator;
import org.datavec.hadoop.records.reader.mapfile.record.RecordWritableCreator;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Alex on 29/05/2017.
 */
public class MapFileReader<V extends Writable> implements Closeable {

    private MapFile.Reader reader;
    private IndexToKey indexToKey;
    private RecordCreator recordCreator;



    public MapFileReader(String path) throws Exception {
        this(path, new LongIndexToKey(), new RecordWritableCreator());
    }

    public MapFileReader(String path, IndexToKey indexToKey, RecordCreator recordCreator) throws Exception {

        this.indexToKey = indexToKey;
        this.recordCreator = recordCreator;
        reader = new MapFile.Reader(new Path(path), null, null);
    }

    public long numRecords(){
        try{
            return indexToKey.getNumRecords(reader);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public V getRecord(long index) throws IOException {
        V v = (V)reader.get(indexToKey.getKeyForIndex(index), recordCreator.newRecord());
        return v;
    }


    @Override
    public void close() throws IOException {
        reader.close();
    }
}

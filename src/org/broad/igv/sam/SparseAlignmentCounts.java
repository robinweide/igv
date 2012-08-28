/*
 * Copyright (c) 2007-2012 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

package org.broad.igv.sam;

import org.apache.log4j.Logger;
import org.broad.igv.util.collections.IntArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jim Robinson
 * @date 11/22/11
 */
public class SparseAlignmentCounts extends BaseAlignmentCounts {

    private static Logger log = Logger.getLogger(SparseAlignmentCounts.class);
    private int maxCount = 0;
    List<Integer> indices;


    /**
     * Map of genomic position -> index of count arrays
     */
    HashMap<Integer, Integer> indexMap;

    IntArrayList posA;
    IntArrayList posT;
    IntArrayList posC;
    IntArrayList posG;
    IntArrayList posN;
    IntArrayList negA;
    IntArrayList negT;
    IntArrayList negC;
    IntArrayList negG;
    IntArrayList negN;
    IntArrayList qA;
    IntArrayList qT;
    IntArrayList qC;
    IntArrayList qG;
    IntArrayList qN;
    IntArrayList posTotal;
    IntArrayList negTotal;
    IntArrayList del;
    IntArrayList ins;
    private IntArrayList totalQ;


    public SparseAlignmentCounts(int start, int end, AlignmentTrack.BisulfiteContext bisulfiteContext) {
        this(start, end, bisulfiteContext, 1000);
    }

    public SparseAlignmentCounts(int start, int end, AlignmentTrack.BisulfiteContext bisulfiteContext, int initSize) {
        super(start, end, bisulfiteContext);

        indexMap = new HashMap<Integer, Integer>(initSize);
        posA = new IntArrayList(initSize);
        posT = new IntArrayList(initSize);
        posC = new IntArrayList(initSize);
        posG = new IntArrayList(initSize);
        posN = new IntArrayList(initSize);
        posTotal = new IntArrayList(initSize);
        negA = new IntArrayList(initSize);
        negT = new IntArrayList(initSize);
        negC = new IntArrayList(initSize);
        negG = new IntArrayList(initSize);
        negN = new IntArrayList(initSize);
        negTotal = new IntArrayList(initSize);
        qA = new IntArrayList(initSize);
        qT = new IntArrayList(initSize);
        qC = new IntArrayList(initSize);
        qG = new IntArrayList(initSize);
        qN = new IntArrayList(initSize);
        del = new IntArrayList(initSize);
        ins = new IntArrayList(initSize);
        totalQ = new IntArrayList(initSize);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getNumberOfPoints() {
        return indices == null ? 0 : indices.size();
    }

    public int getPosition(int idx) {
        return indices.get(idx);
    }


    public int getMaxCount() {
        return maxCount;
    }

    public int getTotalCount(int pos) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            return getCountFromList(posTotal, idx) + getCountFromList(negTotal, idx);

        }
    }

    public int getNegTotal(int pos) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            return getCountFromList(negTotal, idx);

        }
    }

    public int getPosTotal(int pos) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            return getCountFromList(posTotal, idx);

        }
    }

    public int getTotalQuality(int pos) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            return getCountFromList(totalQ, idx);

        }
    }

    public int getCount(int pos, byte b) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            switch (b) {
                case 'a':
                case 'A':
                    return getCountFromList(posA, idx) + getCountFromList(negA, idx);
                case 't':
                case 'T':
                    return getCountFromList(posT, idx) + getCountFromList(negT, idx);
                case 'c':
                case 'C':
                    return getCountFromList(posC, idx) + getCountFromList(negC, idx);
                case 'g':
                case 'G':
                    return getCountFromList(posG, idx) + getCountFromList(negG, idx);
                case 'n':
                case 'N':
                    return getCountFromList(posN, idx) + getCountFromList(negN, idx);
            }
            log.debug("Unknown nucleotide: " + b);
            return 0;
        }
    }

    private int getCountFromList(IntArrayList list, int idx) {
        return idx < list.size() ? list.get(idx) : 0;
    }

    public int getNegCount(int pos, byte b) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            switch (b) {
                case 'a':
                case 'A':
                    return getCountFromList(negA, idx);
                case 't':
                case 'T':
                    return getCountFromList(negT, idx);
                case 'c':
                case 'C':
                    return getCountFromList(negC, idx);
                case 'g':
                case 'G':
                    return getCountFromList(negG, idx);
                case 'n':
                case 'N':
                    return getCountFromList(negN, idx);
            }
            log.error("Unknown nucleotide: " + b);
            return 0;
        }
    }

    public int getPosCount(int pos, byte b) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            switch (b) {
                case 'a':
                case 'A':
                    return getCountFromList(posA, idx);
                case 't':
                case 'T':
                    return getCountFromList(posT, idx);
                case 'c':
                case 'C':
                    return getCountFromList(posC, idx);
                case 'g':
                case 'G':
                    return getCountFromList(posG, idx);
                case 'n':
                case 'N':
                    return getCountFromList(posN, idx);
            }
            log.error("Unknown nucleotide: " + b);
            return 0;
        }
    }

    public int getDelCount(int pos) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            return getCountFromList(del, idx);
        }
    }


    public int getInsCount(int pos) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            return getCountFromList(ins, idx);
        }
    }

    public int getQuality(int pos, byte b) {
        if (!indexMap.containsKey(pos)) {
            if (log.isDebugEnabled()) {
                log.debug("Position out of range: " + pos + " (valid range - " + start + "-" + end);
            }
            return 0;
        } else {
            int idx = getIndex(pos);
            switch (b) {
                case 'a':
                case 'A':
                    return getCountFromList(qA, idx);
                case 't':
                case 'T':
                    return getCountFromList(qT, idx);
                case 'c':
                case 'C':
                    return getCountFromList(qC, idx);
                case 'g':
                case 'G':
                    return getCountFromList(qG, idx);
                case 'n':
                case 'N':
                    return getCountFromList(qN, idx);
            }
            log.error("Unknown nucleotide: " + posN);
            return 0;
        }

    }

    public int getAvgQuality(int pos, byte b) {
        int count = getCount(pos, b);
        return count == 0 ? 0 : getQuality(pos, b) / count;
    }


    protected void incrementDeletion(int pos, boolean negativeStrand) {
        int idx = getIndex(pos);
        increment(del, idx, 1);
        if (countDeletedBasesCovered) {
            if (negativeStrand) {
                increment(negTotal, idx, 1);
            } else {
                increment(posTotal, idx, 1);
            }
        }
    }

    protected void incrementInsertion(AlignmentBlock insBlock) {
        int pos = insBlock.getStart();
        int idx1 = getIndex(pos);
        // Insertions are between bases.  increment count on either side
        increment(ins, idx1, 1);
        if (pos > 0) {
            int idx2 = getIndex(pos - 1);
            increment(ins, idx2, 1);
        }
    }

    protected void incBlockCounts(AlignmentBlock block, boolean isNegativeStrand) {
        int start = block.getStart();
        byte[] bases = block.getBases();
        if (bases != null) {
            for (int i = 0; i < bases.length; i++) {
                int pos = start + i;
                // NOTE:  the direct access block.qualities is intentional,  profiling reveals this to be a critical bottleneck
                byte q = block.qualities[i];
                // TODO -- handle "=" in cigar string with no read bases
                byte n = bases[i];
                incPositionCount(pos, n, q, isNegativeStrand);
            }
        }
    }


    protected void incPositionCount(int pos, byte b, byte q, boolean isNegativeStrand) {

        int idx = getIndex(pos);
        switch (b) {
            case 'a':
            case 'A':
                if (isNegativeStrand) {
                    increment(negA, idx, 1);
                } else {
                    increment(posA, idx, 1);
                }
                increment(qA, idx, q);
                break;
            case 't':
            case 'T':
                if (isNegativeStrand) {
                    increment(negT, idx, 1);
                } else {
                    increment(posT, idx, 1);
                }
                increment(qT, idx, q);
                break;
            case 'c':
            case 'C':
                if (isNegativeStrand) {
                    increment(negC, idx, 1);
                } else {
                    increment(posC, idx, 1);
                }
                increment(qC, idx, q);
                break;
            case 'g':
            case 'G':
                if (isNegativeStrand) {
                    increment(negG, idx, 1);
                } else {
                    increment(posG, idx, 1);
                }
                increment(qG, idx, q);
                break;
            // Everything else is counted as "N".  This might be an actual "N",  or an ambiguity code
            default:
                if (isNegativeStrand) {
                    increment(negN, idx, 1);
                } else {
                    increment(posN, idx, 1);
                }
                increment(qN, idx, q);

        }

        if (isNegativeStrand) {
            increment(negTotal, idx, 1);
        } else {
            increment(posTotal, idx, 1);
        }
        increment(totalQ, idx, q);

        int pt = idx < posTotal.size() ? posTotal.get(idx) : 0;
        int nt = idx < negTotal.size() ? negTotal.get(idx) : 0;
        maxCount = Math.max(pt + nt, maxCount);

    }

    private int getIndex(int pos) {
        Integer index = indexMap.get(pos);
        if (index == null) {
            index = new Integer(indexMap.size());
            indexMap.put(pos, index);
        }
        return index.intValue();
    }


    private void increment(IntArrayList list, int idx, int delta) {
        if (idx < list.size()) {
            list.set(idx, list.get(idx) + delta);
        } else {
            list.set(idx, delta);
        }
    }

    public void finish() {
        indices = new ArrayList<Integer>(indexMap.keySet());
        Collections.sort(indices);
    }

    public AlignmentCounts merge(AlignmentCounts other, AlignmentTrack.BisulfiteContext bisulfiteContext) {
        if (other.getClass() != this.getClass()) {
            throw new IllegalArgumentException("Cannot merge different types of AlignmentCount instances");
        }
        return SparseAlignmentCounts.merge(this, (SparseAlignmentCounts) other, bisulfiteContext);
    }

    private static SparseAlignmentCounts merge(SparseAlignmentCounts first, SparseAlignmentCounts second, AlignmentTrack.BisulfiteContext bisulfiteContext) {
        if (second.getStart() < first.getStart()) {
            SparseAlignmentCounts tmp = first;
            first = second;
            second = tmp;
        }

        int totalPoints = first.getNumberOfPoints() + second.getNumberOfPoints();

        SparseAlignmentCounts result = new SparseAlignmentCounts(first.getStart(), second.getEnd(), bisulfiteContext, totalPoints);

        addRawCounts(result, first);
        addRawCounts(result, second);
        result.maxCount = Math.max(first.getMaxCount(), second.getMaxCount());
        result.finish();
        return result;
    }

    /**
     * Take raw data from input and place it into result. Indexing is
     * consistent within {@code result}, which may not be the same as input.
     * Previous data is not overwritten
     *
     * @param result
     * @param input
     */
    private static void addRawCounts(SparseAlignmentCounts result, SparseAlignmentCounts input) {

        IntArrayList[] inputArrs = getCountArrs(input);
        IntArrayList[] destArrs = getCountArrs(result);
        IntArrayList destArr;

        for (int arrayPos = 0; arrayPos < input.getNumberOfPoints(); arrayPos++) {
            int genomePos = input.indices.get(arrayPos);
            if (!result.indexMap.containsKey(genomePos)) {
                for (int arnum = 0; arnum < destArrs.length; arnum++) {
                    destArr = destArrs[arnum];
                    destArr.add(inputArrs[arnum].get(arrayPos));
                }
                result.getIndex(genomePos);
            }
        }
    }

    /**
     * Create an array of the arrays we use to keep track of data
     *
     * @param counts
     * @return
     */
    private static IntArrayList[] getCountArrs(SparseAlignmentCounts counts) {

        IntArrayList[] result = {counts.posA, counts.posT, counts.posC, counts.posG, counts.posN,
                counts.negA, counts.negT, counts.negC, counts.negG, counts.negN,
                counts.qA, counts.qT, counts.qC, counts.qG, counts.qN, counts.posTotal, counts.negTotal,
                counts.del, counts.ins, counts.totalQ};
        return result;
    }

}

package io.demoapps.rte_spannable;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;

import java.lang.reflect.ParameterizedType;

import io.demoapps.rte_spannable.spans.BoldSpan;

public class SpanHandler{
    private Context mContext;

    public SpanHandler(Context context){
        mContext = context;
    }

    public void applyStyle(Editable editable, int start, int end, Boolean isActive,int existingESpanStart){
        if (isActive) {
            if (end > start) {
                //
                // User inputs or user selects a range
                BoldSpan[] spans = editable.getSpans(start, end, BoldSpan.class);
                BoldSpan existingESpan = null;
                if (spans.length > 0) {
                    existingESpan = spans[0];
                }

                if (existingESpan == null) {
                    checkAndMergeSpan(editable, start, end, BoldSpan.class);
                } else {
                    //int existingESpanStart = editable.getSpanStart(existingESpan);
                    int existingESpanEnd = editable.getSpanEnd(existingESpan);
                    if (existingESpanStart <= start && existingESpanEnd >= end) {
                        // The selection is just within an existing E span
                        // Do nothing for this case
                        changeSpanInsideStyle(editable, start, end, existingESpan);
                    } else {
                        checkAndMergeSpan(editable, start, end, BoldSpan.class);
                    }
                }
            } else {
                //
                // User deletes
                BoldSpan[] spans = editable.getSpans(start, end, BoldSpan.class);
                if (spans.length > 0) {
                    BoldSpan span = spans[0];
                    int lastSpanStart = editable.getSpanStart(span);
                    for (BoldSpan e : spans) {
                        int lastSpanStartTmp = editable.getSpanStart(e);
                        if (lastSpanStartTmp > lastSpanStart) {
                            lastSpanStart = lastSpanStartTmp;
                            span = e;
                        }
                    }

                    int eStart = editable.getSpanStart(span);
                    int eEnd = editable.getSpanEnd(span);
                    //Util.log("eSpan start == " + eStart + ", eSpan end == " + eEnd);

                    if (eStart >= eEnd) {
                        editable.removeSpan(span);
                        extendPreviousSpan(editable, eStart);

                        //setChecked(false);
                        //ARE_Helper.updateCheckStatus(this, false);
                    } else {
                        //
                        // Do nothing, the default behavior is to extend
                        // the span's area.
                    }
                }
            }
        } else {
            //
            // User un-checks the style
            if (end > start) {
                //
                // User inputs or user selects a range
                BoldSpan[] spans = editable.getSpans(start, end, BoldSpan.class);
                if (spans.length > 0) {
                    BoldSpan span = spans[0];
                    if (null != span) {
                        //
                        // User stops the style, and wants to show
                        // un-UNDERLINE characters
                        int ess = editable.getSpanStart(span); // ess == existing span start
                        int ese = editable.getSpanEnd(span); // ese = existing span end
                        if (start >= ese) {
                            // User inputs to the end of the existing e span
                            // End existing e span
                            editable.removeSpan(span);
                            editable.setSpan(span, ess, start - 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        } else if (start == ess && end == ese) {
                            // Case 1 desc:
                            // *BBBBBB*
                            // All selected, and un-check e
                            editable.removeSpan(span);
                        } else if (start > ess && end < ese) {
                            // Case 2 desc:
                            // BB*BB*BB
                            // *BB* is selected, and un-check e
                            editable.removeSpan(span);
                            BoldSpan spanLeft = new BoldSpan();
                            editable.setSpan(spanLeft, ess, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            BoldSpan spanRight = new BoldSpan();
                            editable.setSpan(spanRight, end, ese, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        } else if (start == ess && end < ese) {
                            // Case 3 desc:
                            // *BBBB*BB
                            // *BBBB* is selected, and un-check e
                            editable.removeSpan(span);
                            BoldSpan newSpan = new BoldSpan();
                            editable.setSpan(newSpan, end, ese, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        } else if (start > ess && end == ese) {
                            // Case 4 desc:
                            // BB*BBBB*
                            // *BBBB* is selected, and un-check e
                            editable.removeSpan(span);
                            BoldSpan newSpan = new BoldSpan();
                            editable.setSpan(newSpan, ess, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                    }
                }
            } else if (end == start) {
                //
                // User changes focus position
                // Do nothing for this case
            } else {
                //
                // User deletes
                BoldSpan[] spans = editable.getSpans(start, end, BoldSpan.class);
                if (spans.length > 0) {
                    BoldSpan span = spans[0];
                    if (null != span) {
                        int eStart = editable.getSpanStart(span);
                        int eEnd = editable.getSpanEnd(span);

                        if (eStart >= eEnd) {
                            //
                            // Invalid case, this will never happen.
                        } else {
                            //
                            // Do nothing, the default behavior is to extend
                            // the span's area.
                            // The proceeding characters should be also
                            // UNDERLINE
                            /*setChecked(true);
                            ARE_Helper.updateCheckStatus(this, true);*/
                        }
                    }
                }
            }
        }
    }

    private void checkAndMergeSpan(Editable editable, int start, int end, Class<BoldSpan> clazzE) {
        BoldSpan leftSpan = null;
        BoldSpan[] leftSpans = editable.getSpans(start, start, clazzE);
        if (leftSpans.length > 0) {
            leftSpan = leftSpans[0];
        }

        BoldSpan rightSpan = null;
        BoldSpan[] rightSpans = editable.getSpans(end, end, clazzE);
        if (rightSpans.length > 0) {
            rightSpan = rightSpans[0];
        }


        int leftSpanStart = editable.getSpanStart(leftSpan);
        int rightSpanEnd = editable.getSpanEnd(rightSpan);
        removeAllSpans(editable, start, end, BoldSpan.class);
        if (leftSpan != null && rightSpan != null) {
            BoldSpan eSpan = new BoldSpan();
            editable.setSpan(eSpan, leftSpanStart, rightSpanEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        } else if (leftSpan != null && rightSpan == null) {
            BoldSpan eSpan = new BoldSpan();
            editable.setSpan(eSpan, leftSpanStart, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        } else if (leftSpan == null && rightSpan != null) {
            BoldSpan eSpan = new BoldSpan();
            editable.setSpan(eSpan, start, rightSpanEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        } else {
            BoldSpan eSpan = new BoldSpan();
            editable.setSpan(eSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
    }

    protected void changeSpanInsideStyle(Editable editable, int start, int end, BoldSpan e) {
        // Do nothing by default
        Log.e("ARE", "in side a span!!");
    }

    protected void extendPreviousSpan(Editable editable, int pos) {
        // Do nothing by default
    }

    private void removeAllSpans(Editable editable, int start, int end, Class<BoldSpan> clazzE) {
        BoldSpan[] allSpans = editable.getSpans(start, end, clazzE);
        for (BoldSpan span : allSpans) {
            editable.removeSpan(span);
        }
    }
}

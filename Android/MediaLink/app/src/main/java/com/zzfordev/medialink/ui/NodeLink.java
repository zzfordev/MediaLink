package com.zzfordev.medialink.ui;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zzfordev.medialink.nodes.Node;

import java.util.ArrayList;

public class NodeLink extends ViewGroup
{
    private ArrayList<Node> mNodeList;

    public NodeLink(Context context)
    {
        super(context);

        setOnDragListener(new DragEventListener());
    }


    public void addNode(Node node)
    {
        mNodeList.add(node);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {

    }


    ////
    protected class DragEventListener implements View.OnDragListener
    {
        public boolean onDrag(View v, DragEvent event) {

            final int action = event.getAction();

            switch(action)
            {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                    {

                        //v.setColorFilter(Color.BLUE);

                        v.invalidate();

                        return true;
                    }

                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setColorFilter(Color.GREEN);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setColorFilter(Color.BLUE);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.invalidate();

                    if (event.getResult()) {

                    } else {

                    }
                    return true;

                default:
                    break;
            }

            return false;
        }
    };

}

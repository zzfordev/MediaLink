package com.zzfordev.medialink.ui;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.zzfordev.medialink.nodes.Node;

public class NodeBlock extends android.support.v7.widget.AppCompatImageView
{
    private Node mNodePeer;

    public NodeBlock(Context context)
    {
        super(context);

        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String className = mNodePeer.getClass().getName();

                ClipData dragData = ClipData.newPlainText("aaa",className);

                View.DragShadowBuilder myShadow = new DragShadowBuilder(NodeBlock.this);

                 // Starts the drag
                //v.startDragAndDrop(dragData,myShadow,null,0);
                v.startDrag(dragData,  // the data to be dragged
                         myShadow,  // the drag shadow builder
                         null,      // no need to use local data
                         0          // flags (not currently used, set to 0)
                 );

                return true;
             }
        });
    }

    ////
    private static class DragShadowBuilder extends View.DragShadowBuilder
    {
        private static Drawable _shadow;

        public DragShadowBuilder(View v)
        {
            super(v);
            _shadow = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onProvideShadowMetrics (Point size, Point touch)
        {
            int width = getView().getWidth() / 2;
            int height = getView().getHeight() / 2;

            _shadow.setBounds(0, 0, width, height);

            size.set(width, height);
            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas)
        {
            _shadow.draw(canvas);
        }
    }
}

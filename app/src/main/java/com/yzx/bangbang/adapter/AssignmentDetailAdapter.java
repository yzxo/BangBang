package com.yzx.bangbang.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.R;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.utils.NetWork.Retro;
import com.yzx.bangbang.utils.util;
import com.yzx.bangbang.widget.EvaluateView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.Assignment;

import android.support.v7.widget.RecyclerView.ViewHolder;

public class AssignmentDetailAdapter extends RecyclerView.Adapter<ViewHolder> {
    //public AssignmentDetailAdapter adapter = this;

    public AssignmentDetailAdapter(AssignmentDetail context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        int[] layout = {R.layout.asm_detail_header, R.layout.asm_detail_bid, R.layout.asm_detail_comment};
        View v = context.getLayoutInflater().inflate(layout[type], viewGroup, false);
        if (type == TYPE_HEADER)
            return new HeaderHolder(v);
        if (type == TYPE_BID)
            return new BidHolder(v);
        if (type == TYPE_COMMENT)
            return new CommentHolder(v);
        return null;
    }



    @SuppressWarnings("all")
    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        if (holder instanceof HeaderHolder) {
            HeaderHolder h = (HeaderHolder) holder;
            h.title.setText(assignment.getTitle());
            h.content.setText(assignment.getContent());
            h.date.setText(util.transform_date(assignment.getDate()));
            h.price.setText(util.s(assignment.getPrice()));
            h.price.setTextColor(util.price_color(assignment.getPrice()));
            h.num_servants.setText(assignment.getServants() + "");
            h.status.setText(asm_status[assignment.getStatus()]);

            //h.status.setTextColor(status_color[assignment.getStatus()]);
        } else if (holder instanceof CommentHolder) {
            CommentHolder h = (CommentHolder) holder;
            i--;
            h.content.setText(comments.get(i).content);
            h.date.setText(util.transform_date(comments.get(i).date));
            h.host_name.setText(comments.get(i).poster_name);
            h.num_comments.setText(comments.get(i).floors + "");
            h.host_portrait.setImageURI(Retro.get_portrait_uri(comments.get(i).getPosterId()));
        } else if (holder instanceof BidHolder) {
            BidHolder h = (BidHolder) holder;
            Bid bid = bids.get(i - 1);
            h.host_name.setText(bid.host_name);
            h.evaluate_view.setEvaluate(bid.evaluate);
            h.day_time.setText("在" + bid.day_time + "天内");
            h.price.setText("¥" + bid.price);
            h.host_portrait.setImageURI(Retro.get_portrait_uri(bid.host_id));
        }
    }

    @Override
    public int getItemCount() {
        return comments.size() + Math.min(MAX_BID_SHOWED, bids.size()) + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        if (position > 3) return TYPE_COMMENT;
        return position > bids.size() ? TYPE_COMMENT : TYPE_BID;
    }

    public static class HeaderHolder extends ViewHolder {
        public HeaderHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.num_servants)
        TextView num_servants;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.button_all_bid)
        TextView button_all_bid;
//        @Optional
//        @OnClick({R.id.collect, R.id.subscribe, R.id.button_servants, R.id.host_portrait})
//        void click(View v) {
//            if (listener != null)
//                listener.click(v);
//        }
    }

    public static class CommentHolder extends ViewHolder {

        public CommentHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.host_name)
        TextView host_name;
        @BindView(R.id.num_comments)
        TextView num_comments;
        @BindView(R.id.host_portrait)
        SimpleDraweeView host_portrait;
    }

    public static class BidHolder extends ViewHolder {

        public BidHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @BindView(R.id.host_name)
        TextView host_name;
        @BindView(R.id.host_portrait)
        SimpleDraweeView host_portrait;
        @BindView(R.id.day_time)
        TextView day_time;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.evaluate_view)
        EvaluateView evaluate_view;
    }

//    private static class HeaderHolder extends ViewHolder{
//
//        public HeaderHolder(View itemView) {
//            super(itemView);
//        }
//    }


    public static ClickListener listener;

    public interface ClickListener {
        void click(View v);
    }

    private List<Comment> comments = new ArrayList<>();
    private List<Bid> bids = new ArrayList<>();
    public Assignment assignment;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_BID = 1;
    private static final int TYPE_COMMENT = 2;
    private static final int MAX_BID_SHOWED = 3;
    private static final String[] asm_status = {"打开", "进行中", "结束", "关闭"};
    private static final int[] status_color = {
            Color.parseColor("#2AB049"),
            Color.parseColor("#2AB049"),
            Color.parseColor("#F6F6F6"),
            Color.parseColor("#FF0000")};
    private AssignmentDetail context;

    //setter getter
    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }
}

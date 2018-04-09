package com.yzx.bangbang.presenter;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yzx.bangbang.interfaces.network.IAssignmentDetail;
import com.yzx.bangbang.activity.AssignmentDetail;
import com.yzx.bangbang.model.Bid;
import com.yzx.bangbang.model.Comment;
import com.yzx.bangbang.utils.netWork.Retro;


import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;

/**
 * Created by Administrator on 2018/3/14.
 */

public class AssignmentDetailPresenter {

    private AssignmentDetail context;

    public AssignmentDetailPresenter(AssignmentDetail context) {
        this.context = context;
    }

    public void get_assignment_by_id(int id, Consumer<Assignment> consumer) {
        Retro.inst().create(IAssignmentDetail.class)
                .get_assignment_by_id(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<Assignment>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_comment(int id, Consumer<List<Comment>> consumer) {
        Retro.withList().create(IAssignmentDetail.class)
                .get_comment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<List<Comment>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_sub_comment(int id, Consumer<List<Comment>> consumer) {
        Retro.withList().create(IAssignmentDetail.class)
                .get_sub_comment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<List<Comment>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void post_comment(Comment comment, Consumer<Integer> consumer) {
        Retro.inst().create(IAssignmentDetail.class)
                .post_comment(new Gson().toJson(comment))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_bids(int id, Consumer<List<Bid>> consumer) {
        Retro.withList().create(IAssignmentDetail.class)
                .get_bids(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<List<Bid>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void post_bid(Bid bid, Consumer<Integer> consumer) {
        Retro.inst().create(IAssignmentDetail.class)
                .post_bid(new Gson().toJson(bid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void get_on_going_bid(int asm_id,Consumer<List<Bid>> consumer){
        Retro.withList().create(IAssignmentDetail.class)
                .get_on_going_bid(asm_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(context.<List<Bid>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    public void detach() {
        context = null;
    }
}

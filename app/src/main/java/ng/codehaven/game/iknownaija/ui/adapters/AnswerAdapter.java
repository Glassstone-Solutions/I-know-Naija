package ng.codehaven.game.iknownaija.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.game.iknownaija.R;

/**
 * Created by Thompson on 26/10/2015.
 */
public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    JSONArray json;
    Context context;

    HashMap<String, Integer> answerMap;

    int answerPosition = -1;
    int wrongPosition = -1;

    private AnswerInterface answerInterface;

    public AnswerAdapter(Context c, JSONArray answers) {
        this.json = answers;
        this.context = c;
    }

    public AnswerInterface getAnswerInterface() {
        return answerInterface;
    }

    public void setAnswerInterface(AnswerInterface answerInterface) {
        this.answerInterface = answerInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        AnswerViewHandler avh = new AnswerViewHandler(v);
        avh.getmRoot().setOnClickListener(this);
        return avh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            bindAnswer((AnswerViewHandler) holder, position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindAnswer(AnswerViewHandler holder, int position) throws JSONException {
        holder.getmAnswerView().setText(json.getString(position));
        checkAnswer(holder.getmGood(), isAnswer(position));
        checkWrong(holder.getmWrong(), isWrong(position));
        holder.getmRoot().setTag(position);
    }

    private void checkWrong(ImageView iv, boolean wrong) {
        if (wrong){
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.GONE);
        }
    }

    private void checkAnswer(ImageView iv, boolean answered){
        if (answered){
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.GONE);
        }
    }

    private boolean isWrong(int position) {
        return wrongPosition != -1 && position == wrongPosition;
    }

    private boolean isAnswer(int position){
        return answerPosition != -1 && position == answerPosition;
    }

    @Override
    public int getItemCount() {
        return json.length();
    }

    @Override
    public void onClick(View v) {
        int id = (int) v.getTag();
        getAnswerInterface().onAnswerClick(id);
    }

    public void updateAnswer(int position) {
        answerPosition = position;
        notifyItemChanged(position);
    }

    public void updateAnswerWithFail(int clickedPosition, int answerPosition) {
        updateAnswer(answerPosition);
        wrongPosition = clickedPosition;
        notifyItemChanged(clickedPosition);
    }

    public interface AnswerInterface {
        void onAnswerClick(int position);
    }

    protected class AnswerViewHandler extends RecyclerView.ViewHolder {
        @InjectView(R.id.answer)
        TextView mAnswerView;
        @InjectView(R.id.answer_root)
        LinearLayout mRoot;
        @InjectView(R.id.good_indicator)
        ImageView mGood;
        @InjectView(R.id.wrong_indicator)
        ImageView mWrong;

        public AnswerViewHandler(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }

        public TextView getmAnswerView() {
            return mAnswerView;
        }

        public void setmAnswerView(TextView mAnswerView) {
            this.mAnswerView = mAnswerView;
        }

        public LinearLayout getmRoot() {
            return mRoot;
        }

        public void setmRoot(LinearLayout mRoot) {
            this.mRoot = mRoot;
        }

        public ImageView getmGood() {
            return mGood;
        }

        public void setmGood(ImageView mGood) {
            this.mGood = mGood;
        }

        public ImageView getmWrong() {
            return mWrong;
        }

        public void setmWrong(ImageView mWrong) {
            this.mWrong = mWrong;
        }
    }
}

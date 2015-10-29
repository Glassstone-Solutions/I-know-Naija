package ng.codehaven.game.iknownaija.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ng.codehaven.game.iknownaija.R;
import ng.codehaven.game.iknownaija.models.Category;
import ng.codehaven.game.iknownaija.ui.views.CustomTextView;
import ng.codehaven.game.iknownaija.utils.UIUtils;


public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnClickListener {

    public static final int TYPE_HEADER = 0;

    public static final int TYPE_CATEGORIES = 1;

    private static final int MIN_ITEMS_COUNT = 1;

    private final int cellSize;

    private Context c;

    private List<Category> categories;

    private OnCategoryClickInteraction onCategoryClickInteraction;

    public void setOnCategoryClickInteraction(OnCategoryClickInteraction onCategoryClickInteraction){
        this.onCategoryClickInteraction = onCategoryClickInteraction;
    }

    public CategoriesAdapter(Context context, List<Category> categories) {
        this.c = context;
        this.categories = categories;
        this.cellSize = UIUtils.getScreenWidth(context) / 2;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_HEADER == viewType) {
            View view = LayoutInflater.from(c).inflate(R.layout.view_category_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            return new CategoryHeaderViewHolder(view);
        } else if (TYPE_CATEGORIES == viewType) {
            View view = LayoutInflater.from(c).inflate(R.layout.item_category, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.height = cellSize;
            layoutParams.width = cellSize;
            layoutParams.setFullSpan(false);
            view.setLayoutParams(layoutParams);
            CategoryViewHolder cvh = new CategoryViewHolder(view);

            cvh.getmCatRoot().setOnClickListener(this);

            return cvh;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (TYPE_HEADER == viewType){

            bindHeader((CategoryHeaderViewHolder) holder, position);

        } else if (TYPE_CATEGORIES == viewType){
            bindCategories((CategoryViewHolder) holder, position);
        }
    }

    private void bindHeader(CategoryHeaderViewHolder holder, int position) {

    }

    private void bindCategories(CategoryViewHolder holder, int position) {
        holder.getmCatLabel().setText(categories.get(position - MIN_ITEMS_COUNT).getTitle());
        holder.getmCatRoot().setTag(position);
    }


    @Override
    public int getItemCount() {
        return MIN_ITEMS_COUNT + categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_CATEGORIES;
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        onCategoryClickInteraction.onCategoryClick(categories.get(position - MIN_ITEMS_COUNT));
    }

    static class CategoryHeaderViewHolder extends ViewHolder {
        public CategoryHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    static class CategoryViewHolder extends ViewHolder {
        @InjectView(R.id.txt_cat_label)
        CustomTextView mCatLabel;

        @InjectView(R.id.cat_root)FrameLayout mCatRoot;

        public CategoryViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public CustomTextView getmCatLabel() {
            return mCatLabel;
        }

        public void setmCatLabel(CustomTextView mCatLabel) {
            this.mCatLabel = mCatLabel;
        }

        public FrameLayout getmCatRoot() {
            return mCatRoot;
        }

        public void setmCatRoot(FrameLayout mCatRoot) {
            this.mCatRoot = mCatRoot;
        }
    }

    public interface OnCategoryClickInteraction{
        void onCategoryClick(Category c);
    }
}

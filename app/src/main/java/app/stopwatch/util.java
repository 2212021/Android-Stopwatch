package app.stopwatch;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class util {

    static public class threads {
        static public abstract class Thread extends java.lang.Thread {
            Thread thread;
            ui.UIUpdater updater;
            public Thread(AppCompatActivity activity) {
                super();
                thread = this;
                updater = new ui.UIUpdater(activity) {
                    @Override
                    protected void updateAction(Bundle updateData) {
                        thread.updateAction(updateData);
                    }
                };
            }
            public void update(Bundle updateData) {
                updater.update(updateData);
            }
            public void update() {
                updater.update();
            }
            @Override
            public abstract void run();
            protected abstract void updateAction(Bundle updateData);
            @Override
            public synchronized void start() {
                super.start();
            }
            public synchronized void sleepFor(long milliseconds) throws InterruptedException {
                java.lang.Thread.sleep(milliseconds);
            }
            public synchronized void sleepFor(long milliseconds, int nanoseconds) throws InterruptedException {
                java.lang.Thread.sleep(milliseconds,nanoseconds);
            }
            public void interrupt() {
                super.interrupt();
            }
            public boolean isInterrupted() {
                return super.isInterrupted();
            }
            public boolean isRunning() {
                return this.isAlive() && !this.isInterrupted();
            }
        }

        static public abstract class Timer extends TimerTask {
            Timer task;
            java.util.Timer timer;
            ui.UIUpdater updater;
            public Timer(AppCompatActivity activity) {
                timer = new java.util.Timer();
                task = this;
                updater = new ui.UIUpdater(activity) {
                    @Override
                    protected void updateAction(Bundle updateData) {
                        task.updateAction();
                    }
                };
            }
            public void start(long period) {
                timer.schedule(this,0,period);
            }
            public void start(long delay, long period) {
                timer.schedule(this,delay,period);
            }
            public void scheduleStart(Date startTime, long period) {
                timer.schedule(this,startTime,period);
            }
            public void stop() {
                timer.cancel();
                this.cancel();
            }
            public boolean cancel() {
                return super.cancel();
            }
            @Override
            public void run() {
                updater.update();
            }
            protected abstract void updateAction();
        }

    }

    static public class ui {

        static public abstract class MessageDialog {
            private final AlertDialog dialog;
            private final TextView messageTextView;

            public MessageDialog(AppCompatActivity activity, String title, String message) {
                this(activity, title, message, "OK", 0);
            }

            public MessageDialog(AppCompatActivity activity, String title, String message, int icon) {
                this(activity, title, message, "OK", icon);
            }

            public MessageDialog(AppCompatActivity activity, String title, String message, String dismissButtonText) {
                this(activity, title, message, dismissButtonText, 0);
            }

            public MessageDialog(AppCompatActivity activity, String title, String message, String dismissButtonText, int icon) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                TextView titleTextView = new TextView(activity);
                if (title != null && !title.equals("")) {
                    titleTextView.setText(title);
                }
                messageTextView = new TextView(activity);
                if (message != null && !message.equals("")) {

                    messageTextView.setText(message);
                }
                LinearLayout layout = new LinearLayout(activity);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(titleTextView);
                layout.addView(messageTextView);
                dialog.setView(layout);
                dialog.setPositiveButton(dismissButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onDismiss();
                    }
                });
                if (icon != 0) {
                    dialog.setIcon(icon);
                }
                this.dialog = dialog.create();
            }

            public void open() {
                dialog.show();
            }

            public void setMessage(String message) {
                messageTextView.setText(message);
            }

            protected abstract void onDismiss();
        }

        static public abstract class OptionDialog {
            private final AlertDialog dialog;

            public OptionDialog(AppCompatActivity activity, String title, String message, String button1Text, String button2Text) {
                this(activity, title, message, button1Text, button2Text, null, 0);
            }

            public OptionDialog(AppCompatActivity activity, String title, String message, String button1Text, String button2Text, int icon) {
                this(activity, title, message, button1Text, button2Text, null, icon);
            }

            public OptionDialog(AppCompatActivity activity, String title, String message, String button1Text, String button2Text, String button3Text) {
                this(activity, title, message, button1Text, button2Text, button3Text, 0);
            }

            public OptionDialog(AppCompatActivity activity, String title, String message, String button1Text, String button2Text, String button3Text, int icon) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                TextView titleTextView = null, messageTextView = null;
                LinearLayout layout = new LinearLayout(activity);
                layout.setOrientation(LinearLayout.VERTICAL);
                if (title != null && !title.equals("")) {
                    titleTextView = new TextView(activity);
                    titleTextView.setText(title);
                    layout.addView(titleTextView);
                }
                if (message != null && !message.equals("")) {
                    messageTextView = new TextView(activity);
                    messageTextView.setText(message);
                    layout.addView(messageTextView);
                }
                dialog.setView(layout);
                dialog.setPositiveButton(button1Text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        button1Action();
                        finalAction();
                    }
                });
                dialog.setNeutralButton(button2Text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        button2Action();
                        finalAction();
                    }
                });
                if (button3Text != null) {
                    dialog.setNegativeButton(button3Text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            button3Action();
                            finalAction();
                        }
                    });
                }
                if (icon != 0) {
                    dialog.setIcon(icon);
                }
                this.dialog = dialog.create();
            }

            public void open() {
                dialog.show();
            }

            protected abstract void button1Action();

            protected abstract void button2Action();

            protected abstract void button3Action();

            protected abstract void finalAction();
        }

        static public abstract class TextInputDialog {
            private final AlertDialog dialog;
            private final EditText editText;

            public TextInputDialog(AppCompatActivity activity, String title, int inputType, String proceedButtonText, String dismissButtonText) {
                this(activity, title, inputType, proceedButtonText, dismissButtonText, 0);
            }

            public TextInputDialog(AppCompatActivity activity, String title, int inputType, String proceedButtonText, String dismissButtonText, int icon) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                TextView titleTextView = null;
                this.editText = new EditText(activity);
                editText.setInputType(inputType);
                if (title != null && !title.equals("")) {
                    titleTextView = new TextView(activity);
                    titleTextView.setText(title);
                }
                LinearLayout layout = new LinearLayout(activity);
                layout.setOrientation(LinearLayout.VERTICAL);
                if (titleTextView != null) {
                    layout.addView(titleTextView);
                }
                layout.addView(editText);
                dialog.setView(layout);
                dialog.setPositiveButton(proceedButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onProceed(editText.getText().toString());
                    }
                });
                dialog.setNegativeButton(dismissButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onDismiss(editText.getText().toString());
                    }
                });
                if (icon != 0) {
                    dialog.setIcon(icon);
                }
                this.dialog = dialog.create();
            }

            public void open() {
                dialog.show();
            }

            protected abstract void onProceed(String inputText);

            protected abstract void onDismiss(String inputText);
        }

        static public abstract class ProgressDialog {
            private final AlertDialog dialog;
            private final String title;
            private final TextView titleTextView;
            private final ProgressBar progressBar;

            public ProgressDialog(AppCompatActivity activity, String title) {
                this(activity, title, "Cancel");
            }

            public ProgressDialog(AppCompatActivity activity, String title, int icon) {
                this(activity, title, "Cancel", icon);
            }

            public ProgressDialog(AppCompatActivity activity, String title, String cancelButtonText) {
                this(activity, title, cancelButtonText, 0);
            }

            public ProgressDialog(AppCompatActivity activity, String title, String cancelButtonText, int icon) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                this.title = title;
                this.titleTextView = new TextView(activity);
                this.titleTextView.setText(title);
                this.progressBar = new ProgressBar(activity);
                LinearLayout layout = new LinearLayout(activity);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(titleTextView);
                layout.addView(progressBar);
                dialog.setView(layout);
                dialog.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onCancel();
                    }
                });
                if (icon != 0) {
                    dialog.setIcon(icon);
                }
                this.dialog = dialog.create();
                setProgress(0);
            }

            public void open() {
                dialog.show();
            }

            @SuppressLint("SetTextI18n")
            public void setProgress(int progress) {
                if (progress > 0 && Build.VERSION.SDK_INT >= 24) {
                    progressBar.setProgress(progress, true);
                } else {
                    progressBar.setProgress(progress);
                }
                titleTextView.setText(title + " (" + progress + "%)");
                if (progress < 0 || progress > 100) dialog.cancel();
            }

            public int getProgress() {
                return progressBar.getProgress();
            }

            public void finish() {
                setProgress(-1);
            }

            protected abstract void onCancel();
        }

        static public abstract class UIUpdater {
            protected AppCompatActivity activity;
            protected Handler updater;

            public UIUpdater(AppCompatActivity activity) {
                this.activity = activity;
                this.updater = new Handler(activity.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        updateAction(msg.getData());
                    }
                };
            }

            public void update(Bundle updateData) {
                if (updateData == null) update();
                Message updateMessage = new Message();
                updateMessage.setData(updateData);
                updater.sendMessage(updateMessage);
            }

            public void update() {
                update(new Bundle());
            }

            abstract protected void updateAction(Bundle updateData);
        }

        static public abstract class ListAdapter<ItemDataType, ViewHolderClass extends ListAdapter.ItemViewHolder<ItemDataType, ViewHolderClass>> extends RecyclerView.Adapter<ViewHolderClass> {
            RecyclerView viewGroup;
            AppCompatActivity activity;
            int layout;
            GridLayoutManager layoutManager;
            LayoutInflater layoutInflater;
            ViewHolderClass viewHolder = null;
            ArrayList<ItemDataType> data;
            ArrayList<Boolean> selectionState;
            Comparator<ItemDataType> sortingComparator = null;
            Filter<ItemDataType> filter = null;
            boolean selectionMode = false;
            ActionMode actionBar = null;
            ActionMode.Callback actionBarHandler = null;

            public ListAdapter(AppCompatActivity activity, RecyclerView view, @LayoutRes int layout, @MenuRes int actionMenu) {
                this(activity, view, layout, actionMenu, 1);
            }

            public ListAdapter(AppCompatActivity activity, RecyclerView view, @LayoutRes int layout, @MenuRes int actionMenu, int numColumns) {
                super();
                this.viewGroup = view;
                this.activity = activity;
                this.layout = layout;
                if (view.getAdapter() != this) this.viewGroup.setAdapter(this);
                this.layoutManager = new GridLayoutManager(activity, numColumns);
                this.viewGroup.setLayoutManager(this.layoutManager);
                DividerItemDecoration divider = new DividerItemDecoration(activity, layoutManager.getOrientation());
                viewGroup.addItemDecoration(divider);
                data = new ArrayList<ItemDataType>(0);
                //this.layoutManager = viewGroup.getLayoutManager();
                //this.layoutManager = new GridLayoutManager(activity,1);
                //this.viewGroup.setLayoutManager(this.layoutManager);
                this.layoutInflater = LayoutInflater.from(activity);
                this.actionBarHandler = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        activity.getMenuInflater().inflate(actionMenu, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        onPrepareActionBar(actionMode, menu);
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem itemView) {
                        boolean actionHandled = onActionMenuItemClicked(actionMode, itemView, itemView.getItemId());
                        if (actionHandled) closeActionBar();
                        return actionHandled;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        actionBar = null;
                        deselectAll();
                    }
                };
            }

            public ListAdapter(AppCompatActivity activity, RecyclerView view, ItemDataType[] initData, Comparator<ItemDataType> sortingComparator, Filter<ItemDataType> filter, @LayoutRes int layout, @MenuRes int actionMenu) {
                this(activity, view, initData, sortingComparator, filter, layout, actionMenu, 1);
            }

            public ListAdapter(AppCompatActivity activity, RecyclerView view, ItemDataType[] initData, Comparator<ItemDataType> sortingComparator, Filter<ItemDataType> filter, @LayoutRes int layout, @MenuRes int actionMenu, int numColumns) {
                this(activity, view, layout, actionMenu, numColumns);
                setData(initData, sortingComparator, filter);
            }

            public ListAdapter(AppCompatActivity activity, RecyclerView view, List<ItemDataType> initData, Comparator<ItemDataType> sortingComparator, Filter<ItemDataType> filter, @LayoutRes int layout, @MenuRes int actionMenu) {
                this(activity, view, initData, sortingComparator, filter, layout, actionMenu, 1);
            }

            public ListAdapter(AppCompatActivity activity, RecyclerView view, List<ItemDataType> initData, Comparator<ItemDataType> sortingComparator, Filter<ItemDataType> filter, @LayoutRes int layout, @MenuRes int actionMenu, int numColumns) {
                this(activity, view, layout, actionMenu, numColumns);
                setData(initData, sortingComparator, filter);
            }

            public void setData(ItemDataType[] data) {
                setData(data, null, null);
            }

            public void setData(ItemDataType[] data, Comparator<ItemDataType> sortingComparator) {
                setData(data, sortingComparator, null);
            }

            public void setData(ItemDataType[] data, Filter<ItemDataType> filter) {
                setData(data, null, filter);
            }

            public void setData(ItemDataType[] data, Comparator<ItemDataType> sortingComparator, Filter<ItemDataType> filter) {
                setData(Arrays.asList(data), sortingComparator, filter);
            }

            public void setData(List<ItemDataType> items) {
                setData(items, null, null);
            }

            public void setData(List<ItemDataType> items, Comparator<ItemDataType> sortingComparator) {
                setData(items, sortingComparator, null);
            }

            public void setData(List<ItemDataType> items, Filter<ItemDataType> filter) {
                setData(items, null, filter);
            }

            public void setData(List<ItemDataType> items, Comparator<ItemDataType> sortingComparator, Filter<ItemDataType> filter) {
                if (items == null) return;
                if (filter != null) this.filter = filter;
                if (sortingComparator != null) this.sortingComparator = sortingComparator;
                new threads.Thread(activity) {
                    @Override
                    public void run() {
                        selectionState = new ArrayList<>();
                        if (ListAdapter.this.filter == null) {
                            data = new ArrayList<>(items);
                            for (ItemDataType i : items) {
                                selectionState.add(false);
                            }
                        } else {
                            data = new ArrayList<>();
                            for (ItemDataType item : items) {
                                if (ListAdapter.this.filter.condition(item)) {
                                    data.add(item);
                                    selectionState.add(false);
                                }
                            }
                        }
                        if (ListAdapter.this.sortingComparator != null) {
                            Collections.sort(data, ListAdapter.this.sortingComparator);
                        }
                        update();
                    }

                    @Override
                    protected void updateAction(Bundle updateData) {
                        notifyDataSetChanged();
                        goToTop();
                    }
                }.start();
            }

            public void addItem(ItemDataType item) {
                ArrayList<ItemDataType> l = new ArrayList<>();
                l.add(item);
                addItems(l);
            }

            public void addItems(ItemDataType[] items) {
                addItems(Arrays.asList(items));
            }

            public void addItems(ItemDataType[] items, Filter<ItemDataType> filter) {
                addItems(Arrays.asList(items));
            }

            public void addItems(List<ItemDataType> items) {
                new threads.Thread(activity) {
                    @Override
                    public void run() {
                        Bundle update = new Bundle();
                        ArrayList<ItemDataType> items_;
                        ArrayList<Boolean> selectionState_ = new ArrayList<>();
                        ;
                        if (filter != null) {
                            items_ = new ArrayList<>();
                            for (ItemDataType item : items) {
                                if (filter.condition(item)) {
                                    items_.add(item);
                                    selectionState_.add(false);
                                }
                            }
                        } else {
                            items_ = new ArrayList<>(items);
                            for (ItemDataType i : items) {
                                selectionState_.add(false);
                            }
                        }
                        if (sortingComparator != null) {
                            Collections.sort(items_, sortingComparator);
                            int index = 0;
                            while (index < data.size()) {
                                if (sortingComparator.compare(items_.get(items_.size() - 1), data.get(index)) < 0) {
                                    data.addAll(index, items_);
                                    selectionState.addAll(index, selectionState_);
                                    update.putInt("index", index);
                                    break;
                                }
                                index++;
                            }
                            if (index == data.size()) {
                                data.addAll(items_);
                                selectionState.addAll(selectionState_);
                                update.putInt("index", data.size() - 1);
                            }
                        } else {
                            data.addAll(items_);
                            selectionState.addAll(selectionState_);
                            update.putInt("index", data.size() - 1);
                        }
                        update(update);
                    }

                    @Override
                    protected void updateAction(Bundle updateData) {
                        int positionStart = updateData.getInt("index");
                        notifyItemRangeInserted(positionStart, items.size());
                        goTo(positionStart);
                    }
                }.start();
            }

            public ArrayList<ItemDataType> getData() {
                return data;
            }

            public int getItemIndex(ItemDataType item) {
                return data.indexOf(item);
            }

            public ItemDataType getItemAtIndex(int index) {
                return data.get(index);
            }

            public void setItem(ItemDataType item, ItemDataType itemValue) {
                int index = getItemIndex(item);
                if (index >= 0) {
                    setItem(index, itemValue);
                }
            }

            public void setItem(int index, ItemDataType itemValue) {
                data.set(index, itemValue);
                selectionState.set(index, false);
                notifyItemChanged(index);
            }

            public void deleteItem(int index) {
                data.remove(index);
                selectionState.remove(index);
                notifyItemRemoved(index);
            }

            public void clear() {
                data.clear();
                selectionState.clear();
                notifyDataSetChanged();
            }

            public void disableSorting(Comparator<ItemDataType> sortingComparator) {
                this.sortingComparator = null;
                notifyDataSetChanged();
            }

            public interface Filter<ItemDataType> {
                boolean condition(ItemDataType item);
            }

            public void goTo(int index) {
                viewGroup.scrollToPosition(index);
            }

            public void goToTop() {
                viewGroup.scrollToPosition(0);
            }

            public void scrollToTop() {
                viewGroup.smoothScrollToPosition(0);
            }

            public void scrollToBottom() {
                viewGroup.smoothScrollToPosition(getItemCount() - 1);
            }

            public void scrollTo(int index) {
                viewGroup.smoothScrollToPosition(index);
            }

            @NonNull
            @Override
            public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View itemView = layoutInflater.inflate(layout, viewGroup, false);
                viewHolder = createViewHolder(itemView, layout);
                viewHolder.attachListAdapter(this);
                return viewHolder;
            }

            public void setLayout(@LayoutRes int layout) {
                this.layout = layout;
                viewGroup.setAdapter(this);
            }

            public void setLayoutToList(@LayoutRes int layout) {
                this.layout = layout;
                layoutManager.setSpanCount(1);
                viewGroup.setAdapter(this);
            }

            public void setLayoutToGrid(@LayoutRes int layout, int numColumns) {
                this.layout = layout;
                layoutManager.setSpanCount(numColumns);
                viewGroup.setAdapter(this);
            }

            public void setNumColumns(int numColumns) {
                layoutManager.setSpanCount(numColumns);
                viewGroup.setAdapter(this);
            }

            public int getNumColumns() {
                return layoutManager.getSpanCount();
            }

            @Override
            public int getItemCount() {
                if (data != null) return data.size();
                else return 0;
            }

            public boolean isSelected(int index) {
                return selectionState.get(index);
            }

            public void setSelected(int index) {
                setSelected(index, false);
            }

            public void setSelected(int index, boolean redrawAll) {
                if (allItemsDeselected()) {
                    selectionMode = true;
                    showActionBar();
                    redrawAll = true;
                    onActivateSelectionMode();
                }
                selectionState.set(index, true);
                if (redrawAll)
                    notifyDataSetChanged();
                else
                    notifyItemChanged(index);
            }

            protected void selectAll() {
                if (allItemsDeselected()) {
                    selectionMode = true;
                    showActionBar();
                    onActivateSelectionMode();
                }
                for (int index = 0; index < selectionState.size(); index++) {
                    selectionState.set(index, true);
                }
                notifyDataSetChanged();
            }

            public void deselect(int index) {
                deselect(index, false);
            }

            public void deselect(int index, boolean redrawAll) {
                selectionState.set(index, false);
                if (allItemsDeselected()) redrawAll = true;
                if (redrawAll)
                    notifyDataSetChanged();
                else
                    notifyItemChanged(index);
                if (allItemsDeselected()) {
                    selectionMode = false;
                    closeActionBar();
                    onDeactivateSelectionMode();
                }
            }

            protected void deselectAll() {
                if (selectionState != null) {
                    for (int index = 0; index < selectionState.size(); index++) {
                        selectionState.set(index, false);
                    }
                    notifyDataSetChanged();
                }
                selectionMode = false;
                closeActionBar();
                onDeactivateSelectionMode();
            }

            public void switchSelection(int index) {
                switchSelection(index, false);
            }

            public void switchSelection(int index, boolean redrawAll) {
                if (isSelected(index)) {
                    deselect(index, redrawAll);
                } else {
                    setSelected(index, redrawAll);
                }
            }

            public ArrayList<Integer> getSelectedIndices() {
                final ArrayList<Integer> selectedIndices = new ArrayList<Integer>();
                for (int index = 0; index < data.size(); index++) {
                    boolean selected = selectionState.get(index);
                    if (selected) {
                        selectedIndices.add(index);
                    }
                }
                return selectedIndices;
            }

            public ArrayList<ItemDataType> getSelectedItems() {
                ArrayList<ItemDataType> selectedItems = new ArrayList<ItemDataType>();
                for (int index = 0; index < data.size(); index++) {
                    boolean selected = selectionState.get(index);
                    if (selected) selectedItems.add(data.get(index));
                }
                return selectedItems;
            }

            public ArrayList<Integer> getUnselectedIndices() {
                ArrayList<Integer> unselectedIndices = new ArrayList<Integer>();
                for (int index = 0; index < data.size(); index++) {
                    boolean selected = selectionState.get(index);
                    if (!selected) unselectedIndices.add(index);
                }
                return unselectedIndices;
            }

            public ArrayList<ItemDataType> getUnselectedItems() {
                ArrayList<ItemDataType> unselectedItems = new ArrayList<ItemDataType>();
                for (int index = 0; index < data.size(); index++) {
                    boolean selected = selectionState.get(index);
                    if (!selected) unselectedItems.add(data.get(index));
                }
                return unselectedItems;
            }

            public boolean allItemsDeselected() {
                for (boolean selected : selectionState) {
                    if (selected) return false;
                }
                return true;
            }

            public boolean allItemsSelected() {
                for (boolean selected : selectionState) {
                    if (!selected) return false;
                }
                return true;
            }

            public boolean isInSelectionMode() {
                return selectionMode;
            }

            public void redrawAll() {
                notifyDataSetChanged();
            }

            private void showActionBar() {
                actionBar = activity.startActionMode(actionBarHandler);
            }

            private void closeActionBar() {
                if (actionBar != null) actionBar.finish();
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolderClass viewHolder, int index) {
                new threads.Thread(activity) {
                    @Override
                    public void run() {
                        update(prepareItemViewUpdate(viewHolder, index));
                    }

                    @Override
                    protected void updateAction(Bundle updateData) {
                        applyItemViewUpdate(updateData, viewHolder, index);
                    }
                }.start();
            }

            public static abstract class ItemViewHolder<ItemDataType, ViewHolderClass extends ItemViewHolder<ItemDataType, ViewHolderClass>> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
                private ListAdapter<ItemDataType, ViewHolderClass> listAdapter;

                public ItemViewHolder(@NonNull View itemView) {
                    super(itemView);
                    activateClickListener(itemView);
                }

                public void attachListAdapter(ListAdapter<ItemDataType, ViewHolderClass> listAdapter) {
                    this.listAdapter = listAdapter;
                }

                public void activateClickListener(View view) {
                    view.setOnClickListener(this);
                    view.setOnLongClickListener(this);
                }

                public int index() {
                    return this.getAdapterPosition();
                }

                public ItemDataType getItem() {
                    return this.listAdapter.getItemAtIndex(this.index());
                }

                public ArrayList<ItemDataType> getItems() {
                    return this.listAdapter.getData();
                }

                public void setItem(ItemDataType item) {
                    this.listAdapter.setItem(this.index(), item);
                }

                public void setItems(ItemDataType[] data) {
                    this.listAdapter.setData(data);
                }

                public void setItems(ItemDataType[] data, Filter<ItemDataType> filter) {
                    this.listAdapter.setData(data, filter);
                }

                public void setItems(List<ItemDataType> data) {
                    this.listAdapter.setData(data);
                }

                public void setItems(List<ItemDataType> data, Filter<ItemDataType> filter) {
                    this.listAdapter.setData(data, filter);
                }

                public boolean isSelected() {
                    return listAdapter.isSelected(this.index());
                }

                public void setSelected() {
                    listAdapter.setSelected(this.index());
                }

                public void setSelected(boolean redrawAll) {
                    listAdapter.setSelected(this.index(), redrawAll);
                }

                public void deselect(int index) {
                    listAdapter.deselect(this.index());
                }

                public void deselect(int index, boolean redrawAll) {
                    listAdapter.deselect(this.index(), redrawAll);
                }

                protected void deselectAll() {
                    listAdapter.deselectAll();
                }

                public void switchSelection() {
                    listAdapter.switchSelection(this.index());
                }

                public void switchSelection(boolean redrawAll) {
                    listAdapter.switchSelection(this.index(), redrawAll);
                }

                public boolean isInSelectionMode() {
                    return listAdapter.isInSelectionMode();
                }

                public void redraw() {
                    listAdapter.notifyItemChanged(this.index());
                }

                public void redrawAll() {
                    listAdapter.redrawAll();
                }

                public void openPopupMenu(@MenuRes int menu, View anchor) {
                    PopupMenu popupMenu = new PopupMenu(listAdapter.activity, anchor);
                    popupMenu.inflate(menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem itemView) {
                            boolean actionHandled = onPopupMenuItemClick(index(), itemView, itemView.getItemId());
                            if (actionHandled) popupMenu.dismiss();
                            return actionHandled;
                        }
                    });
                    popupMenu.show();
                }

                @Override
                public void onClick(View view) {
                    onItemClick(this.index(), view.getId());
                }

                public boolean onLongClick(View view) {
                    return onItemLongClick(this.index(), view.getId());
                }

                protected abstract void onItemClick(int index, @IdRes int viewId);

                protected abstract boolean onItemLongClick(int index, @IdRes int viewId);

                protected abstract boolean onPopupMenuItemClick(int index, MenuItem menuItem, int menuItemId);
            }

            protected abstract ViewHolderClass createViewHolder(View itemView, @LayoutRes int layout);

            protected abstract Bundle prepareItemViewUpdate(ViewHolderClass viewHolder, int index);

            protected abstract void applyItemViewUpdate(Bundle update, ViewHolderClass viewHolder, int index);

            protected abstract void onActivateSelectionMode();

            protected abstract void onDeactivateSelectionMode();

            public abstract void onPrepareActionBar(ActionMode actionMode, Menu menu);

            protected abstract boolean onActionMenuItemClicked(ActionMode menu, MenuItem itemView, @IdRes int itemId);
        }

    }

    static public class database {

        static public class DatabaseTable {
            protected SQLiteDatabase db;
            protected String table;
            protected String[] keys;
            protected Type[] types;
            public enum Type {Integer,Float,String}
            public DatabaseTable(AppCompatActivity activity, String dbName, String tableName, Type[] types, String[] keys, @Nullable String primaryKey) {
                this.table = tableName;
                this.keys = keys;
                this.types = types;
                String path = activity.getApplicationInfo().dataDir + "/" + dbName+".db";
                StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table + "(");
                for (int index=0; index<keys.length; index++) {
                    String key = keys[index];
                    Type type = types[index];
                    query.append(key);
                    switch (type) {
                        case Integer:
                            query.append(" INT");
                            break;
                        case Float:
                            query.append(" REAL");
                            break;
                        case String:
                            query.append(" TEXT");
                            break;
                    }
                    if (key.equals(primaryKey)) query.append(" PRIMARY KEY");
                    if (index!=keys.length-1) query.append(",");
                }
                query.append(");");
                db = SQLiteDatabase.openOrCreateDatabase(path,null);
                db.execSQL(query.toString());
            }
            public DatabaseTable(AppCompatActivity activity, String dbName, String tableName, Type[] types, String[] keys) {
                this(activity, dbName, tableName, types, keys,null);
            }
            public void addRow(HashMap<String,?> rowEntries) {
                if (rowEntries==null) {
                    addRow();
                    return;
                }
                Set<String> keys = rowEntries.keySet();
                StringBuilder query = new StringBuilder("INSERT INTO " + table + " (");
                Iterator<String> iterator;
                iterator = keys.iterator();
                while (iterator.hasNext()) {
                    query.append(iterator.next());
                    if (iterator.hasNext()) query.append(",");
                }
                query.append(") VALUES (");
                iterator = keys.iterator();
                while (iterator.hasNext()) {
                    query.append(rowEntries.get(iterator.next()));
                    if (iterator.hasNext()) query.append(",");
                }
                query.append(");");
                db.execSQL(query.toString());
            }
            public void addRow() {
                String query = "INSERT INTO " + table + " DEFAULT VALUES;";
                db.execSQL(query);
            }
            public void removeRow(String idKey, String idKeyValue) {
                String query = "DELETE FROM " + table + " WHERE " + idKey + " = " + idKeyValue;
                db.execSQL(query);
            }
            public void setEntry(int rowIndex, String entryKey, int entryValue) {
                setEntry(rowIndex,entryKey,String.valueOf(entryValue));
            }
            public void setEntry(int rowIndex, String entryKey, float entryValue) {
                setEntry(rowIndex,entryKey,String.valueOf(entryValue));
            }
            public void setEntry(int rowIndex, String entryKey, String entryValue) {
                String currentValue = getStringEntry(rowIndex,entryKey);
                setEntry(entryKey,currentValue,entryKey,entryValue);
            }
            public void setEntry(String idKey, int idKeyValue, String entryKey, int entryValue) {
                setEntry(idKey,String.valueOf(idKeyValue),entryKey,String.valueOf(entryValue));
            }
            public void setEntry(String idKey, float idKeyValue, String entryKey, int entryValue) {
                setEntry(idKey,String.valueOf(idKeyValue),entryKey,String.valueOf(entryValue));
            }
            public void setEntry(String idKey, String idKeyValue, String entryKey, int entryValue) {
                setEntry(idKey,idKeyValue,entryKey,String.valueOf(entryValue));
            }
            public void setEntry(String idKey, int idKeyValue, String entryKey, float entryValue) {
                setEntry(idKey,String.valueOf(idKeyValue),entryKey,String.valueOf(entryValue));
            }
            public void setEntry(String idKey, float idKeyValue, String entryKey, float entryValue) {
                setEntry(idKey,String.valueOf(idKeyValue),entryKey,String.valueOf(entryValue));
            }
            public void setEntry(String idKey, String idKeyValue, String entryKey, float entryValue) {
                setEntry(idKey,idKeyValue,entryKey,String.valueOf(entryValue));
            }
            public void setEntry(String idKey, int idKeyValue, String entryKey, String entryValue) {
                setEntry(idKey,String.valueOf(idKeyValue),entryKey,entryValue);
            }
            public void setEntry(String idKey, float idKeyValue, String entryKey, String entryValue) {
                setEntry(idKey,String.valueOf(idKeyValue),entryKey,entryValue);
            }
            public void setEntry(String idKey, String idKeyValue, String entryKey, String entryValue) {
                String equals1, equals2;
                if (entryValue==null || entryValue.equalsIgnoreCase("NULL")) {
                    equals1 = " IS ";
                }
                else {
                    equals1 = " = ";
                    if (types[getColumnIndex(entryKey)]==Type.String) entryValue = '"'+entryValue+'"';
                }
                if (idKeyValue==null || idKeyValue.equalsIgnoreCase("NULL")) {
                    equals2 = " IS ";
                }
                else {
                    equals2 = " = ";
                    if (types[getColumnIndex(idKey)]==Type.String) idKeyValue = '"'+idKeyValue+'"';
                }
                String query = "UPDATE " + table + " SET " + entryKey + equals1 + entryValue
                                + " WHERE " + idKey + equals2 + idKeyValue + ";";
                db.execSQL(query);
            }
            public Integer getIntEntry(int rowIndex, String entryKey) {
                Integer value;
                Cursor rowCursor = row(rowIndex);
                int columnIndex = getColumnIndex(entryKey);
                if (rowCursor.isNull(columnIndex)) {
                    value = null;
                }
                else {
                    value = rowCursor.getInt(columnIndex);
                }
                rowCursor.close();
                return value;
            }
            public Integer getIntEntry(String idKey, String idKeyValue, String entryKey) {
                Integer value;
                Cursor rowCursor = row(idKey,idKeyValue);
                int columnIndex = getColumnIndex(entryKey);
                if (rowCursor.isNull(columnIndex)) {
                    value = null;
                }
                else {
                    value = rowCursor.getInt(columnIndex);
                }
                rowCursor.close();
                return value;
            }
            public Float getFloatEntry(int rowIndex, String entryKey) {
                Float value;
                Cursor rowCursor = row(rowIndex);
                int columnIndex = getColumnIndex(entryKey);
                if (rowCursor.isNull(columnIndex)) {
                    value = null;
                }
                else {
                    value = rowCursor.getFloat(columnIndex);
                }
                rowCursor.close();
                return value;
            }
            public Float getFloatEntry(String idKey, String idKeyValue, String entryKey) {
                Float value;
                Cursor rowCursor = row(idKey,idKeyValue);
                int columnIndex = getColumnIndex(entryKey);
                if (rowCursor.isNull(columnIndex)) {
                    value = null;
                }
                else {
                    value = rowCursor.getFloat(columnIndex);
                }
                rowCursor.close();
                return value;
            }
            public String getStringEntry(int rowIndex, String entryKey) {
                String value;
                Cursor rowCursor = row(rowIndex);
                int columnIndex = getColumnIndex(entryKey);
                if (rowCursor.isNull(columnIndex)) {
                    value = null;
                }
                else {
                    value = rowCursor.getString(columnIndex);
                }
                rowCursor.close();
                return value;
            }
            public String getStringEntry(String idKey, String idKeyValue, String entryKey) {
                String value;
                Cursor rowCursor = row(idKey,idKeyValue);
                int columnIndex = getColumnIndex(entryKey);
                if (rowCursor.isNull(columnIndex)) {
                    value = null;
                }
                else {
                    value = rowCursor.getString(columnIndex);
                }
                rowCursor.close();
                return value;
            }
            public int rows() {
                Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + table + ";",null);
                cursor.moveToFirst();
                return cursor.getInt(0);
            }
            public boolean contains(String idKey, String value) {
                boolean exists = false;
                for (int index=0; index<rows(); index++) {
                    if (getStringEntry(index,idKey).equals(value)) {
                        exists = true;
                        break;
                    }
                }
                return exists;
            }
            protected Cursor row(String idKey, String idKeyValue) {
                Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + idKey + " = " + idKeyValue + ";",null);
                cursor.moveToFirst();
                return cursor;
            }
            protected Cursor row(int rowIndex) {
                Cursor cursor = db.rawQuery("SELECT * FROM " + table + " ORDER BY ROWID ASC LIMIT " + rowIndex+1 + ";",null);
                cursor.moveToLast();
                return cursor;
            }
            protected int getColumnIndex(String key) {
                for (int index=0; index<keys.length; index++) {
                    if (keys[index].equals(key)) {
                        return index;
                    }
                }
                return -1;
            }
        }

        static public class DatabaseSettingsTable {
            DatabaseTable table;
            public DatabaseSettingsTable(AppCompatActivity activity, String dbName, String tableName, DatabaseTable.Type[] types, String[] keys) {
                table = new DatabaseTable(activity, dbName, tableName, types, keys);
                table.addRow();
            }
            public void setEntry(String entryKey, int entryValue) {
                table.setEntry(0,entryKey,entryValue);
            }
            public void setEntry(String entryKey, float entryValue) {
                table.setEntry(0,entryKey,entryValue);
            }
            public void setEntry(String entryKey, String entryValue) {
                table.setEntry(0,entryKey,entryValue);
            }
            public Integer getIntEntry(String entryKey) {
                return table.getIntEntry(0,entryKey);
            }
            public Float getFloatEntry(String entryKey) {
                return table.getFloatEntry(0,entryKey);
            }
            public String getStringEntry(String entryKey) {
                return table.getStringEntry(0,entryKey);
            }
        }

    }
}

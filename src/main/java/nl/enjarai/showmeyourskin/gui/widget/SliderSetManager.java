package nl.enjarai.showmeyourskin.gui.widget;

import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;

import java.util.List;

public class SliderSetManager {
    private final List<SliderSetTab> tabs;
    private SliderSetTab activeTab;
    private TabSwitchCallback tabSwitchCallback;

    public SliderSetManager(int startIndex, List<SliderSetTab> tabs) {
        this.tabs = tabs;
        if (tabs.size() == 0) {
            throw new IllegalArgumentException("Cannot create a SliderSetManager with no tabs.");
        }
        activeTab = tabs.get(startIndex);
        for (SliderSetTab tab : tabs) {
            tab.setContainerActive(tab == activeTab);
            tab.setManager(this);
        }
    }

    public SliderSetManager(int startIndex, SliderSetTab... tabs) {
        this(startIndex, List.of(tabs));
    }

    public void setActiveTab(SliderSetTab tab) {
        if (!tabs.contains(tab)) {
            throw new IllegalArgumentException("Cannot select a tab that is not in the manager.");
        }
        tabSwitchCallback.onTabSwitch(tab);
        activeTab = tab;
        for (SliderSetTab t : tabs) {
            t.setContainerActive(t == tab);
        }
    }

    public SliderSetTab getActiveTab() {
        return activeTab;
    }

    public List<SliderSetTab> getTabs() {
        return tabs;
    }

    public void setTabSwitchCallback(TabSwitchCallback tabSwitchCallback) {
        this.tabSwitchCallback = tabSwitchCallback;
    }

    public void addChildrenTo(SpruceContainerWidget container) {
        for (var tab : tabs) {
            container.addChild(tab);
            container.addChild(tab.getContainer());
        }
    }

    public int getActiveIndex() {
        return tabs.indexOf(activeTab);
    }

    public interface TabSwitchCallback {
        void onTabSwitch(SliderSetTab tab);
    }
}

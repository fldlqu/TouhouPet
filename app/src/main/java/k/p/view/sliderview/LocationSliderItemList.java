package k.p.view.sliderview;

import k.p.action.SearchAction;
import k.p.domain.BasePet;
import k.p.domain.states.ActiveState;
import k.p.location.Location;
import k.p.main.R;
import k.p.services.DialogService;
import k.p.services.LocationService;
import k.p.services.StateService;
import k.p.services.ViewService;

/* JADX INFO: loaded from: classes.dex */
public class LocationSliderItemList extends SliderItemList {
    protected Location location;

    public LocationSliderItemList(Location location, SliderView sliderView, SliderCanvas sc) {
        super(sliderView, sc);
        this.location = location;
    }

    @Override // k.p.view.sliderview.SliderItemList
    public void init() {
        super.init();
        refreshLocation();
    }

    public void refreshLocation() {
        clearSliderItemView();
        SliderItemView button = new BaseSliderTitleTextButton(this.sliderView, this.location.getName());
        addSliderItemView(button);
        for (final Location l : this.location.getNearbyList()) {
            if (l.isVisible()) {
                SliderItemView button2 = new BaseSliderTextButton(this.sliderView, l.getName()) { // from class: k.p.view.sliderview.LocationSliderItemList.1
                    @Override // k.p.view.sliderview.BaseSliderTextButton, k.p.view.sliderview.SliderItemView
                    public void onClick() {
                        if (this.sliderView.mainService.pet.getCurrentState() instanceof ActiveState) {
                            this.sliderView.currentSliderItemList = this.sliderView.locationMap.get(l);
                            l.onEnter();
                        } else {
                            String str = "当前正在" + this.sliderView.mainService.pet.getCurrentState().getStateDoingDescription() + "\r\n确定要移动至" + l.getName() + "吗?";
                            final Location location = l;
                            DialogService.confirm("移动", str, new DialogService.CallBack() { // from class: k.p.view.sliderview.LocationSliderItemList.1.1
                                @Override // k.p.services.DialogService.CallBack
                                public void onReturn(boolean retVal) {
                                    if (retVal) {
                                        LocationSliderItemList.this.sliderView.mainService.pet.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL);
                                        LocationSliderItemList.this.sliderView.currentSliderItemList = LocationSliderItemList.this.sliderView.locationMap.get(location);
                                        location.onEnter();
                                    }
                                }
                            });
                        }
                    }
                };
                addSliderItemView(button2);
            }
        }
        SliderItemView button3 = new BaseSliderButton(this.sliderView, "属性", R.drawable.button_status) { // from class: k.p.view.sliderview.LocationSliderItemList.2
            @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
            public void onClick() {
                switch (ViewService.statusView.stage) {
                    case 0:
                        ViewService.statusView.hide();
                        break;
                    case 1:
                        ViewService.statusView.show();
                        break;
                }
            }
        };
        addSliderItemView(button3);
        SliderItemView button4 = new BaseSliderButton(this.sliderView, "回家", R.drawable.button_home) { // from class: k.p.view.sliderview.LocationSliderItemList.3
            @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
            public void onClick() {
                if (this.sliderView.mainService.pet.getCurrentState() instanceof ActiveState) {
                    this.sliderView.currentSliderItemList = this.sliderView.mainList;
                    this.sliderView.mainService.pet.setCurrentLocation(LocationService.HOME);
                    return;
                }
                DialogService.confirm("回家", "当前正在" + this.sliderView.mainService.pet.getCurrentState().getStateDoingDescription() + "\r\n确定要回家吗?", new DialogService.CallBack() { // from class: k.p.view.sliderview.LocationSliderItemList.3.1
                    @Override // k.p.services.DialogService.CallBack
                    public void onReturn(boolean retVal) {
                        if (retVal) {
                            LocationSliderItemList.this.sliderView.mainService.pet.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL);
                            LocationSliderItemList.this.sliderView.currentSliderItemList = LocationSliderItemList.this.sliderView.mainList;
                            LocationSliderItemList.this.sliderView.mainService.pet.setCurrentLocation(LocationService.HOME);
                        }
                    }
                });
            }
        };
        addSliderItemView(button4);
        SliderItemView button5 = new BaseSliderButton(this.sliderView, "探索", R.drawable.button_search) { // from class: k.p.view.sliderview.LocationSliderItemList.4
            @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
            public void onClick() {
                this.sliderView.toast((String) new SearchAction().doAction(this.sliderView.mainService.pet, LocationSliderItemList.this.location).getStatus());
            }
        };
        addSliderItemView(button5);
    }
}

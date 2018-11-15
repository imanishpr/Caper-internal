package com.khelacademy.www.pojos;

import java.util.List;

public class Home {
    private List<Banner> banners;

    public Widget getWidgets() {
        return widgets;
    }

    public void setWidgets(Widget widgets) {
        this.widgets = widgets;
    }

    private Widget widgets;

    public List <Banner> getBanners() {
        return banners;
    }

    public void setBanners(List <Banner> banners) {
        this.banners = banners;
    }
}

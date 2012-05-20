//    LS App - LoadSensing Application - https://github.com/Skamp/LS-App
//    
//    Copyright (C) 2011-2012
//    Authors:
//        Sergio González Díez        [sergio.gd@gmail.com]
//        Sergio Postigo Collado      [spostigoc@gmail.com]
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.lsn.LoadSensing.mapsforge;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import org.mapsforge.android.maps.ItemizedOverlay;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.Overlay;
import org.mapsforge.android.maps.OverlayItem;

import com.lsn.LoadSensing.LSNetInfoActivity;
import com.lsn.LoadSensing.element.LSNetwork;
import com.readystatesoftware.mapviewballoons.R;

public class LSNetworksOverlayForge extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private Context c;
	private OverlayItem currentFocussedItem;
	private View clickRegion;
	private int currentFocussedIndex;
	private int viewOffset;
	private MapView mapView;
	private BalloonOverlayViewForge<OverlayItem> balloonView;
	private LinearLayout layout;
	private TextView title;
	private TextView snippet;
	private ArrayList<LSNetwork> m_networks;

	public LSNetworksOverlayForge(Drawable defaultMarker, MapView mapView,ArrayList<LSNetwork> networks) {
		super(boundCenter(defaultMarker));
		this.c = mapView.getContext();
		this.mapView = mapView;
		this.m_networks = networks;
	}

	public void addOverlay(OverlayItem overlay) {
		m_overlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected final boolean onTap(int index) {

		currentFocussedIndex = index;
		currentFocussedItem = createItem(index);

		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() >= 1) {
			hideOtherBalloons(mapOverlays);
		}

		((ViewGroup) mapView.getParent()).removeView(layout);

		MapController mMapController = mapView.getController();
		mMapController.setCenter(currentFocussedItem.getPoint());

		layout = new LinearLayout(c);
		layout.setVisibility(View.VISIBLE);

		LayoutInflater inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.balloon_overlay, layout);

		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);

		layout.setVisibility(View.VISIBLE);
		if (currentFocussedItem.getTitle() != null) {
			title.setVisibility(View.VISIBLE);
			title.setText(currentFocussedItem.getTitle());
		} else {
			title.setVisibility(View.GONE);
		}

		if (currentFocussedItem.getSnippet() != null) {
			snippet.setVisibility(View.VISIBLE);
			snippet.setText(currentFocussedItem.getSnippet());
		} else {
			snippet.setVisibility(View.GONE);
		}

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

		layout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		params.topMargin -= layout.getMeasuredHeight();

		ImageView imgClose = (ImageView)layout.findViewById(R.id.close_img_button);
		imgClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				layout.setVisibility(View.GONE);
				((ViewGroup) mapView.getParent()).removeView(layout);
			}

		});

		clickRegion = layout.findViewById(R.id.balloon_inner_layout);
		clickRegion.setOnTouchListener(createBalloonTouchListener());

		((ViewGroup) mapView.getParent()).addView(layout, params);

		return true;
	}

	protected boolean onBalloonTap(int index, OverlayItem item) {

		Intent i = null;
		i = new Intent(c,LSNetInfoActivity.class);

		if (i!=null){
			Bundle bundle = new Bundle();

			bundle.putParcelable("NETWORK_OBJ", m_networks.get(index));

			i.putExtras(bundle);

			c.startActivity(i);
		}
		return true;
	}

	protected void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
	}

	private void hideOtherBalloons(List<Overlay> overlays) {

		for (Overlay overlay : overlays) {
			if (overlay instanceof LSNetworksOverlayForge && overlay != this) {
				((LSNetworksOverlayForge) overlay).hideBalloon();

			}
		}

	}

	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int[] states = {android.R.attr.state_pressed};
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}
					// call overridden method
					onBalloonTap(currentFocussedIndex, currentFocussedItem);
					return true;
				} else {
					return false;
				}

			}
		};
	}

	protected BalloonOverlayViewForge<OverlayItem> createBalloonOverlayView() {
		return new BalloonOverlayViewForge<OverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
	}

	protected MapView getMapView() {
		return mapView;
	}

	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}
	public int getBalloonBottomOffset() {
		return viewOffset;
	}

	public class BalloonOverlayViewForge<Item extends OverlayItem> extends FrameLayout {

		private LinearLayout layout;
		private TextView title;
		private TextView snippet;

		/**
		 * Create a new BalloonOverlayView.
		 * 
		 * @param context - The activity context.
		 * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
		 * when rendering this view.
		 */
		public BalloonOverlayViewForge(Context context, int balloonBottomOffset) {

			super(context);

			setPadding(10, 0, 10, balloonBottomOffset);
			layout = new LinearLayout(context);
			layout.setVisibility(VISIBLE);

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.balloon_overlay, layout);
			title = (TextView) v.findViewById(R.id.balloon_item_title);
			snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);

			ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
			close.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					layout.setVisibility(GONE);
				}
			});

			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.NO_GRAVITY;

			addView(layout, params);
		}

		/**
		 * Sets the view data from a given overlay item.
		 * 
		 * @param item - The overlay item containing the relevant view data 
		 * (title and snippet). 
		 */
		public void setData(Item item) {

			layout.setVisibility(VISIBLE);
			if (item.getTitle() != null) {
				title.setVisibility(VISIBLE);
				title.setText(item.getTitle());
			} else {
				title.setVisibility(GONE);
			}
			if (item.getSnippet() != null) {
				snippet.setVisibility(VISIBLE);
				snippet.setText(item.getSnippet());
			} else {
				snippet.setVisibility(GONE);
			}

		}

	}
}



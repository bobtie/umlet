package com.umlet.element.experimental.uml.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baselet.control.NewGridElementConstants;
import com.baselet.control.enumerations.Direction;
import com.baselet.diagram.draw.BaseDrawHandler;
import com.baselet.diagram.draw.geom.Point;
import com.baselet.diagram.draw.geom.PointDouble;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.umlet.element.experimental.ElementId;
import com.umlet.element.experimental.NewGridElement;
import com.umlet.element.experimental.Properties;
import com.umlet.element.experimental.settings.Settings;
import com.umlet.element.experimental.settings.SettingsRelation;
import com.umlet.element.experimental.uml.relation.RelationPoints.Selection;

public class Relation extends NewGridElement {

	private RelationPoints relationPoints;
	
	@Override
	public ElementId getId() {
		return ElementId.Relation;
	}

	@Override
	protected void updateConcreteModel(BaseDrawHandler drawer, Properties properties) {
		//		properties.drawPropertiesText();

		relationPoints.drawLinesBetweenPoints(drawer);
	}

	@Override
	protected void updateMetaDrawer(BaseDrawHandler drawer) {
		drawer.clearCache();
		if (isSelected()) {
			drawer.setBackgroundColor(ColorOwn.SELECTION_BG);

			// draw rectangle around whole element (basically a helper for developers to make sure the (invisible) size of the element is correct)
//			drawer.setForegroundColor(ColorOwn.TRANSPARENT);
//			drawer.drawRectangle(0, 0, getRectangle().getWidth(), getRectangle().getHeight());

			drawer.setForegroundColor(ColorOwn.SELECTION_FG);
			relationPoints.drawPointCircles(drawer);
			relationPoints.drawDragBox(drawer);
		}
	}

	@Override
	public void setAdditionalAttributes(String additionalAttributes) {
		super.setAdditionalAttributes(additionalAttributes);
		List<PointDouble> pointList = new ArrayList<PointDouble>();
		String[] split = additionalAttributes.split(";");
		for (int i = 0; i < split.length; i += 2) {
			pointList.add(new PointDouble(Double.valueOf(split[i]), Double.valueOf(split[i+1])));
		}
		relationPoints = new RelationPoints(pointList);
	}
	
	@Override
	public String getAdditionalAttributes() {
		return relationPoints.toAdditionalAttributesString();
	}

	@Override
	public Settings getSettings() {
		return new SettingsRelation(relationPoints);
	}

	@Override
	public void drag(Collection<Direction> resizeDirection, int diffX, int diffY, Point mousePosBeforeDrag, boolean isShiftKeyDown, boolean firstDrag) {
		Point mousePosBeforeDragRelative = new Point(mousePosBeforeDrag.getX() - getRectangle().getX(), mousePosBeforeDrag.getY() - getRectangle().getY());
		int gridSize = (int) (getHandler().getZoomFactor() * NewGridElementConstants.DEFAULT_GRID_SIZE);
		Selection returnSelection = relationPoints.getSelectionAndApplyChanges(mousePosBeforeDragRelative, diffX, diffY, this, gridSize, firstDrag);
		if (returnSelection != Selection.NOTHING) {
			updateModelFromText();
		}
	}
	
	@Override
	public void dragEnd() {
		boolean updateNecessary = relationPoints.removeRelationPointOfCurrentDragIfItOverlaps();
		if (updateNecessary) {
			updateModelFromText();
		}
	}
	
	@Override
	public Set<Direction> getResizeArea(int x, int y) {
		return new HashSet<Direction>();
	}
	
	@Override
	public boolean isSelectableOn(Point point) {
		Point relativePoint = new Point(point.getX() - getRectangle().getX(), point.getY() - getRectangle().getY());
		return relationPoints.getSelection(relativePoint) != Selection.NOTHING;
	}

}


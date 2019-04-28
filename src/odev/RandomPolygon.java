package odev;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import odev.entity.Edge;
import odev.entity.Point;

public class RandomPolygon extends Applet implements MouseListener, MouseMotionListener {
	// size of the applet window
	private final int MAX_WIDTH = 800;
	private final int MAX_HEIGHT = 800;
	// max value for rgb in 24 bit color
	private final int INTENSITIES = 185;
	// the number of different shapes possible
	private int numOfVertices = 20;

	// coordinates of the polygons displayed
	private int[] xCoord;
	private int[] yCoord;

	private int[] finalPolygonXList;
	private int[] finalPolygonYList;

	private Point playerPoint;

	// pseudorandom number generator
	private Random generator = new Random();

	// background color in applet panel
	private Color backColor = new Color(20, 20, 20);
	private Dimension preferredDimension = new Dimension(900, 900);

	private TextField textField;
	private TextField importTextField;

	private List<Point> S;
	private List<Point> finalPolygon;
	private List<List<Point>> layers;

	private long nextSecond = System.currentTimeMillis() + 1000;
	private int framesInLastSecond = 0;
	private int framesInCurrentSecond = 0;

	public static <T> List<T> rotate (List<T> aL, int shift) {
		List<T> newValues = new ArrayList<>(aL);
		Collections.rotate(newValues, shift);
		return newValues;
	}

	public void start () {
//		timer.start();
	}

	public void init () {
		S = new ArrayList<>();
		resize(preferredDimension);
		setPreferredSize(preferredDimension);
		randomize();
		layers = generatePolygon();

		finalPolygon = S;
		finalPolygonXList = getXList(finalPolygon);
		finalPolygonYList = getYList(finalPolygon);
		playerPoint = new Point(250, 250);

		this.setBackground(backColor);
		prepareGUI();
	}

	private void prepareGUI () {
		addMouseListener(this);
		addMouseMotionListener(this);

		Label importLabel = new Label("File name to import: ");
		add(importLabel);

		importTextField = new TextField(20);
		add(importTextField);

		Button importButton = new Button("Import");
		this.add(importButton);
		importButton.addActionListener(new ImportTextListener());

		Label label = new Label("Vertex count: ");
		add(label);

		textField = new TextField(5);
		add(textField);

		Button submit = new Button("Go");
		this.add(submit);
		submit.addActionListener(new ButtonClickListener());
	}

	public void stop () {

	}

	public void paint (Graphics g) {

		g.setColor(Color.GRAY);
		g.fillPolygon(finalPolygonXList, finalPolygonYList, finalPolygon.size());
//		for (List<Point> hull : layers) {
//			g.setColor(Color.MAGENTA);
////			g.drawPolygon(getXList(hull), getYList(hull), hull.size());
//			for (Point p : hull) {
////				g.setColor(Color.WHITE);
//				g.drawString("(" + (int) p.x + "," + (int) p.y + ")", (int) p.x + 5, (int) p.y + 5);
//			}
//		}
		drawPoint(g);

		long currentTime = System.currentTimeMillis();
		if (currentTime > nextSecond) {
			nextSecond += 1000;
			framesInLastSecond = framesInCurrentSecond;
			framesInCurrentSecond = 0;
		}
		framesInCurrentSecond++;
		g.drawString(framesInLastSecond + " fps", 20, 20);
		try {
			Thread.sleep(1000/60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private List<List<Point>> generatePolygon () {
		List<List<Point>> layers = findConvexHullLayers(S);
		//loop through all layers
		if (layers.size() > 1) {
			for (int layerIndex = layers.size() - 1; layerIndex > 0; layerIndex--) {
				List<Point> outerLayer = layers.get(layerIndex - 1);
				List<Point> innerLayer = layers.get(layerIndex);
				int randomEdgeIndex = (int) (Math.random() * (outerLayer.size() - 1));
				Edge outerEdge = new Edge(outerLayer.get(randomEdgeIndex % outerLayer.size()), outerLayer.get((randomEdgeIndex + 1) % outerLayer.size()), randomEdgeIndex % outerLayer.size(), (randomEdgeIndex + 1) % outerLayer.size());
				List<Edge> edges = getEdges(innerLayer);
				Edge nonIntersectingEdge = getVisibleEdge(outerEdge, edges);
				if (nonIntersectingEdge != null) {
					mergeLayers(outerLayer, innerLayer, outerEdge, nonIntersectingEdge);
				}
			}
		}
		finalPolygon = layers.get(0);
		finalPolygonXList = getXList(finalPolygon);
		finalPolygonYList = getYList(finalPolygon);
		return layers;
	}

	private Edge getVisibleEdge (Edge outerEdge, List<Edge> edges) {
		for (Edge innerEdge : edges) {
			boolean intersects = false;
//			g.setColor(backColor);
//			g.fillRect(0, 0, MAX_WIDTH, MAX_HEIGHT);
//			g.setColor(Color.WHITE);
//			g.drawLine((int) outerEdge.p.x, (int) outerEdge.p.y, (int) outerEdge.q.x, (int) outerEdge.q.y);
//			g.setColor(Color.CYAN);
//			g.drawLine((int) innerEdge.p.x, (int) innerEdge.p.y, (int) outerEdge.p.x, (int) outerEdge.p.y);
//			g.drawLine((int) innerEdge.q.x, (int) innerEdge.q.y, (int) outerEdge.q.x, (int) outerEdge.q.y);
//			g.setColor(Color.BLUE);
//			g.drawLine((int) innerEdge.p.x, (int) innerEdge.p.y, (int) innerEdge.q.x, (int) innerEdge.q.y);
			for (Edge currentEdge : edges) {
				if (edges.size() == 1) {
					return innerEdge;
				}
				if (!(innerEdge.p.compareTo(currentEdge.p) == 0 || innerEdge.p.compareTo(currentEdge.q) == 0
						|| innerEdge.q.compareTo(currentEdge.p) == 0 || innerEdge.q.compareTo(currentEdge.q) == 0)) {
//					g.setColor(Color.RED);
//					g.drawLine((int) currentEdge.p.x, (int) currentEdge.p.y, (int) currentEdge.q.x, (int) currentEdge.q.y);
					if (checkIfInterlayerLinesIntersect(innerEdge, outerEdge)) {
						if (Line2D.linesIntersect(innerEdge.p.x, innerEdge.p.y, outerEdge.q.x, outerEdge.q.y, currentEdge.p.x, currentEdge.p.y, currentEdge.q.x, currentEdge.q.y)
								|| Line2D.linesIntersect(innerEdge.q.x, innerEdge.q.y, outerEdge.p.x, outerEdge.p.y, currentEdge.p.x, currentEdge.p.y, currentEdge.q.x, currentEdge.q.y)) {
							intersects = true;
							break;
						}
					} else {
						if (Line2D.linesIntersect(innerEdge.p.x, innerEdge.p.y, outerEdge.p.x, outerEdge.p.y, currentEdge.p.x, currentEdge.p.y, currentEdge.q.x, currentEdge.q.y)
								|| Line2D.linesIntersect(innerEdge.q.x, innerEdge.q.y, outerEdge.q.x, outerEdge.q.y, currentEdge.p.x, currentEdge.p.y, currentEdge.q.x, currentEdge.q.y)) {
							intersects = true;
							break;
						}
					}
				}
			}
			if (!intersects) {
//				g.drawLine((int) innerEdge.p.x, (int) innerEdge.p.y, (int) innerEdge.q.x, (int) innerEdge.q.y);
				return innerEdge;
			}
		}
		return null;
	}

	private boolean checkIfInterlayerLinesIntersect (Edge innerEdge, Edge outerEdge) {
		return Line2D.linesIntersect(innerEdge.p.x, innerEdge.p.y, outerEdge.p.x, outerEdge.p.y, innerEdge.q.x, innerEdge.q.y, outerEdge.q.x, outerEdge.q.y);
	}

	private void mergeLayers (List<Point> layer, List<Point> layerToBeMerged, Edge outerEdge, Edge innerEdge) {
		layerToBeMerged = rotate(layerToBeMerged, -innerEdge.pIndex - 1);
		Collections.reverse(layerToBeMerged);
		layer.addAll(outerEdge.pIndex + 1, layerToBeMerged);
		outerEdge.qIndex += layerToBeMerged.size();
	}

	private List<Edge> getEdges (List<Point> points) {
		List<Edge> edges = new ArrayList<>();
		if (points.size() == 0) {
			return edges;
		} else if (points.size() == 1) {
			edges.add(new Edge(points.get(0), points.get(0), 0, 0));
			return edges;
		}
		for (int i = 0; i < points.size(); i++) {
			if (i == points.size() - 1) {
				edges.add(new Edge(points.get(points.size() - 1), points.get(0), i, 0));
			} else {
				edges.add(new Edge(points.get(i), points.get(i + 1), i, i + 1));
			}
		}
		return edges;
	}

	private int[] getXList (List<Point> points) {
		int size = points.size();
		int[] result = new int[size];
		Integer[] temp = points.stream().map(point -> (int) point.x).collect(Collectors.toList()).toArray(new Integer[size]);
		for (int n = 0; n < size; ++n) {
			result[n] = temp[n];
		}
		return result;
	}

	private int[] getYList (List<Point> points) {
		int size = points.size();
		int[] result = new int[size];
		Integer[] temp = points.stream().map(point -> (int) point.y).collect(Collectors.toList()).toArray(new Integer[size]);
		for (int n = 0; n < size; ++n) {
			result[n] = temp[n];
		}
		return result;
	}

	private List<List<Point>> findConvexHullLayers (List<Point> points) {
		List<Point> setOfPoints = new ArrayList<>(points);
		List<List<Point>> convexHullLayers = new ArrayList<>();

		while (setOfPoints.size() > 0) {
			List<Point> layer = ConvexHull.makeHull(setOfPoints);
			convexHullLayers.add(layer);
			setOfPoints.removeAll(layer);
		}
		return convexHullLayers;
	}

	private void drawPoint (Graphics g) {
		g.setColor(Color.WHITE);
		g.fillOval((int) playerPoint.x - 5, (int) playerPoint.y - 5, 10, 10);
	}

	private void randomize () {
		S.clear();
		xCoord = new int[numOfVertices];
		yCoord = new int[numOfVertices];
		for (int i = 0; i < numOfVertices; i++) {
			xCoord[i] = generator.nextInt(MAX_WIDTH - 50) + 50;
			yCoord[i] = generator.nextInt(MAX_HEIGHT - 50) + 50;
			S.add(new Point(xCoord[i], yCoord[i]));
		}
	}

	private Color randomColor () {
		return (new Color(generator.nextInt(INTENSITIES + 1),
				generator.nextInt(INTENSITIES + 1),
				generator.nextInt(INTENSITIES + 1)));
	}

	private void parseFile (String fileName) {
		Scanner scanner = new Scanner(getClass().getResourceAsStream(fileName));

		String line;
		line = scanner.nextLine();
		String[] dotCoordinates = line.split(",");
		String[] lineCoordinates;
		S.clear();
		playerPoint = new Point(Integer.parseInt(dotCoordinates[0]), Integer.parseInt(dotCoordinates[1]));
		System.out.println("PolygonStart");
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			lineCoordinates = line.split(",");
			S.add(new Point(Integer.parseInt(lineCoordinates[0]), Integer.parseInt(lineCoordinates[1])));
		}
		System.out.println("PolygonEnd");

		xCoord = new int[S.size()];
		yCoord = new int[S.size()];
		for (int i = 0; i < S.size(); i++) {
			xCoord[i] = (int) S.get(i).x;
			yCoord[i] = (int) S.get(i).y;
		}
	}

	public String getAppletInfo () {
		return ("Random Polygon\n");
	}

	@Override
	public void mouseClicked (MouseEvent e) {

	}

	@Override
	public void mousePressed (MouseEvent e) {
		if (playerPoint.contains(finalPolygon, new Point(e.getX(), e.getY()))) {
			playerPoint.x = e.getX();
			playerPoint.y = e.getY();
			repaint();
		}
	}

	@Override
	public void mouseReleased (MouseEvent e) {

	}

	@Override
	public void mouseEntered (MouseEvent e) {

	}

	@Override
	public void mouseExited (MouseEvent e) {

	}

	@Override
	public void mouseDragged (MouseEvent e) {
		if (playerPoint.contains(finalPolygon, new Point(e.getX(), e.getY()))) {
			playerPoint.x = e.getX();
			playerPoint.y = e.getY();
			repaint();
		}
	}

	@Override
	public void mouseMoved (MouseEvent e) {

	}

	class ButtonClickListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			numOfVertices = Integer.parseInt(textField.getText());
			randomize();
			layers = generatePolygon();
			repaint();
		}
	}

	class ImportTextListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			String fileName = importTextField.getText();
			parseFile(fileName);
			layers = generatePolygon();
			repaint();
		}
	}
}
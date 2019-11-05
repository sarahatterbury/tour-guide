/**
 * 
 */
package tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tourguide.Chunk.CreateHeader;

/**
 * @author pbj
 */
public class ControllerImp implements Controller {
	private static Logger logger = Logger.getLogger("tourguide");
	private static final String LS = System.lineSeparator();

	private double waypointRadius;
	private double waypointSeparation;

	private enum Mode {
		browseTour, browseTourDetails, createTour, followTour
	}

	private Mode curMode;

	private Tour curTour;
	private List<Tour> finishedToursList = new ArrayList<Tour>();
	private int curStage;

	private double curNorthing;
	private double curEasting;

	private String startBanner(String messageName) {
		return LS + "-------------------------------------------------------------" + LS + "MESSAGE: " + messageName
				+ LS + "-------------------------------------------------------------";
	}

	public ControllerImp(double waypointRadius, double waypointSeparation) {
		this.waypointRadius = waypointRadius;
		this.waypointSeparation = waypointSeparation;
	}

	// --------------------------
	// Create tour mode
	// --------------------------

	// Some examples are shown below of use of logger calls. The rest of the
	// methods below that correspond
	// to input messages could do with similar calls.

	@Override
	public Status startNewTour(String id, String title, Annotation annotation) {
		logger.fine(startBanner("startNewTour"));

		logger.info("Checking for correct mode, system must be in browse tour mode");
		if (curMode != Mode.browseTour) {
			logger.info("Invalid Mode, system must be in browse tour mode");
			return new Status.Error("Invalid Mode, system must be in browse tour mode");
		}

		curMode = Mode.createTour;
		logger.info("Mode changed to create tour mode");

		curTour = new Tour(id, title, annotation);
		logger.info("Created new tour: " + curTour);

		return Status.OK;
	}

	@Override
	public Status addWaypoint(Annotation annotation) {
		logger.fine(startBanner("addWaypoint"));

		logger.info("Checking for correct mode, system must be in create tour mode");
		if (curMode != Mode.createTour) {
			logger.info("Invalid Mode, system must be in create tour mode");
			return new Status.Error("Invalid Mode, system must be in create tour mode");
		}

		logger.info("Checking waypoints have minimum waypoint separation distance between them");
		int noWaypoints = curTour.getWaypointList().size();
		Waypoint lastWP = curTour.getWaypointList().get(noWaypoints - 1);

		if (noWaypoints != 0) {
			Displacement displacement = new Displacement((lastWP.getEasting() - curEasting),
					(lastWP.getNorthing() - curNorthing));
			double distanceBetween = displacement.distance();

			if (waypointSeparation >= distanceBetween) {
				logger.info("Invalid, Distance between waypoints must be over waypoint separation distance");
				return new Status.Error(
						"Invalid, Distance between waypoints must be over waypoint separation distance");
			}
		}

		logger.info("Adding a waypoint to tour");
		Waypoint waypoint = new Waypoint(annotation, curNorthing, curEasting);
		curTour.getWaypointList().add(waypoint);

		return Status.OK;
	}

	@Override
	public Status addLeg(Annotation annotation) {
		logger.fine(startBanner("addLeg"));

		logger.info("Checking for correct mode, system must be in create tour mode");
		if (curMode != Mode.createTour) {
			logger.info("Invalid Mode, system must be in create tour mode");
			return new Status.Error("Invalid Mode, system must be in create tour mode");
		}

		if (annotation == null) {
			annotation = Annotation.DEFAULT;
		}

		int noWaypoints = curTour.getWaypointList().size();
		int noLegs = curTour.getLegList().size();

		if (noWaypoints < noLegs) {
			logger.info("Invalid, Leg has just been added, add waypoint");
			return new Status.Error("Invalid, Leg has just been added, add waypoint");
		}

		logger.info("Adding a Leg to tour");
		Leg leg = new Leg(annotation);
		curTour.getLegList().add(leg);

		return Status.OK;
	}

	@Override
	public Status endNewTour() {
		logger.fine(startBanner("endNewTour"));

		logger.info("Checking for correct mode, system must be in create tour mode");
		if (curMode != Mode.createTour) {
			logger.info("Invalid Mode, system must be in create tour mode");
			return new Status.Error("Invalid Mode, system must be in create tour mode");
		}

		int noWaypoints = curTour.getWaypointList().size();
		int noLegs = curTour.getLegList().size();

		if (noWaypoints != noLegs) {
			logger.info("Invalid, tour must have the same number of legs and waypoints");
			return new Status.Error("Invalid, tour must have the same number of legs and waypoints");
		}

		if (noWaypoints < 1) {
			logger.info("Invalid, Tour must have at least one waypoint");
			return new Status.Error("Invalid, Tour must have at least one waypoint");
		} else {
			logger.info("Valid - Tour Ending, adding created tour to tour library");
			finishedToursList.add(curTour);

			logger.info("Mode changed back to browse tour mode");
			curMode = Mode.browseTour;
			return Status.OK;
		}
	}

	// --------------------------
	// Browse tours mode
	// --------------------------

	@Override
	public Status showTourDetails(String tourID) {
		logger.fine(startBanner("showTourDetails"));

		logger.info("Checking for correct mode, system must be in browse tour details mode");
		if (curMode != Mode.browseTour) {
			logger.info("Invalid Mode, system must be in browse tour mode");
			return new Status.Error("Invalid Mode, system must be in browse tour mode");
		}

		logger.info("Mode changed to browse tour details mode");
		curMode = Mode.browseTourDetails;

		logger.info("Checking that the tour exists in the tour library");
		if (!finishedToursList.contains(tourID)) {
			logger.info("Invalid, this tour does not exist");
			return new Status.Error("Invalid, this tour does not exist");
		}

		for (int i = 0; i < finishedToursList.size(); i++) {
			if ((finishedToursList.get(i).getId()).equals(tourID)) {
				curTour = finishedToursList.get(i);
				logger.info("Output tour: " + curTour);
			}
		}

		return Status.OK;
	}

	@Override
	public Status showToursOverview() {
		logger.fine(startBanner("showToursOverview"));

		logger.info("Checking for correct mode, system must be in browse tour mode");
		if (curMode != Mode.browseTour) {
			logger.info("Invalid Mode, system must be in browse tour mode");
			return new Status.Error("Invalid Mode, system must be in browse tour mode");
		}

		logger.info("Mode changed to browse tour details mode");
		curMode = Mode.browseTour;

		logger.info("Displays all of the tours in the tour library");
		for (int i = 0; i < finishedToursList.size(); i++) {
			System.out.println(finishedToursList.get(i) + "\n");
			logger.info("Output tours: " + finishedToursList.get(i));
		}
		return Status.OK;
	}

	// --------------------------
	// Follow tour mode
	// --------------------------

	@Override
	public Status followTour(String id) {
		logger.fine(startBanner("followTour"));

		logger.info("Checking for correct mode, system must be in follow tour mode");
		if (curMode != Mode.browseTour || curMode != Mode.browseTourDetails) {
			logger.info("Invalid Mode, system must be in follow tour mode");
			return new Status.Error("Invalid Mode, system must be in follow tour mode");
		}

		logger.info("Mode changed to follow tour mode");
		curMode = Mode.followTour;

		logger.info("Setting the first stage to 0");
		curStage = 0;

		logger.info("Checking that the tour exists in the tour library");
		if (!finishedToursList.contains(id)) {
			logger.info("Invalid, this tour does not exist");
			return new Status.Error("Invalid, this tour does not exist");
		}

		for (int i = 0; i < finishedToursList.size(); i++) {
			if ((finishedToursList.get(i).getId()).equals(id)) {
				curTour = finishedToursList.get(i);
				logger.info("Following tour: " + curTour);
			}
		}
		return Status.OK;
	}

	@Override
	public Status endSelectedTour() {
		logger.fine(startBanner("endSelectedTour"));

		if (curMode != Mode.followTour) {
			logger.info("Invalid Mode, system must be in follow tour mode");
			return new Status.Error("Invalid Mode, system must be in follow tour mode");
		}

		logger.info("Mode changed to browse tour mode");
		curMode = Mode.browseTour;
		showToursOverview();
		return Status.OK;

	}

	// --------------------------
	// Multi-mode methods
	// --------------------------
	@Override
	public void setLocation(double easting, double northing) {

		logger.info("Setting location to (" + curEasting + " , " + curNorthing + ")");

		if (curMode == Mode.followTour) {
			Waypoint nextWP = curTour.getWaypointList().get(curStage);
			Displacement nextDistance = new Displacement((nextWP.getEasting() - easting),
					(nextWP.getNorthing() - northing));

			if (nextDistance.distance() <= waypointRadius) {
				curStage = curStage + 1;
				logger.info("Stage: " + curStage);
			}
		}
	}

	@Override
	public List<Chunk> getOutput() {
		List<Chunk> Output = new ArrayList<Chunk>();

		if (curMode == Mode.createTour) {
			Chunk createChunk = new Chunk.CreateHeader(curTour.getTitle(), curTour.getLegList().size(),
					curTour.getWaypointList().size());
			Output.add(createChunk);
			return Output;
		} else if (curMode == Mode.browseTour) {
			Chunk.BrowseOverview browseOverveiw = new Chunk.BrowseOverview();
			for (int i = 0; i < finishedToursList.size(); i++) {
				browseOverveiw.addIdAndTitle(finishedToursList.get(i).getId(), finishedToursList.get(i).getTitle());
			}
			Output.add(browseOverveiw);
			return Output;
		} else if (curMode == Mode.browseTourDetails) {
			Chunk.BrowseDetails browseDetails = new Chunk.BrowseDetails(curTour.getId(), curTour.getTitle(),
					curTour.getAnnotation());
			Output.add(browseDetails);
			return Output;
		} else if (curMode == Mode.followTour) {
			Chunk followChunk = new Chunk.FollowHeader(curTour.getTitle(), curStage, curTour.getWaypointList().size());
			Output.add(followChunk);

			for (int i = 0; i < curTour.getWaypointList().size(); i++) {
				Displacement curDistance = new Displacement(
						(curTour.getWaypointList().get(i).getEasting() - curEasting),
						(curTour.getWaypointList().get(i).getNorthing() - curNorthing));

				if (curDistance.distance() <= waypointRadius) {
					Chunk followWaypointChunk = new Chunk.FollowWaypoint(
							curTour.getWaypointList().get(i).getAnnotation());
					Output.add(followWaypointChunk);
				}
			}

			if (curStage < curTour.getWaypointList().size()) {
				Chunk followLegChunk = new Chunk.FollowLeg(curTour.getLegList().get(curStage).getAnnotation());
				Output.add(followLegChunk);

				Displacement nextDisplacment = new Displacement(
						(curTour.getWaypointList().get(curStage).getEasting() - curEasting),
						(curTour.getWaypointList().get(curStage).getNorthing() - curNorthing));

				Chunk followBearingChunk = new Chunk.FollowBearing(nextDisplacment.bearing(),
						nextDisplacment.distance());
				Output.add(followBearingChunk);
			}
		}
		return Output;
	}

}

import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom';
import eventService from '../api/EventService';
import participantService from '../api/ParticipantService';
import recombeeInteractions from '../api/RecomebeeInteractions';
import { useAuth } from '../AuthContext';

const EventDetailsPage = () => {
    const { currentUser } = useAuth();
    const userId = currentUser?.uid;
    const { state } = useLocation();
    const recommId = state.recommId;

    const [rsvpStatus, setRsvpStatus] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();
    const [event, setEvent] = useState(state.event || {});

    useEffect(() => {
        const fetchEventDetails = async (eventId) => {
            try {
                const fetchedEvent = await eventService.getEvent(eventId);
                setEvent(fetchedEvent);
            } catch (error) {
                setErrorMessage(error.message || 'Failed to fetch event details.');
            }
        };

        const checkRsvpStatus = async () => {
            if (userId && event.eventId) {
                try {
                    const status = await participantService.getEventParticipantStatus(userId, event.eventId);
                    setRsvpStatus(status === 'Attending');
                } catch (error) {
                    setErrorMessage(error.message || 'Failed to check RSVP status.');
                }
            }
        };

        if (!event.eventId && state.eventId) {
            fetchEventDetails(state.eventId);
        } else {
            checkRsvpStatus();
        }

        if (event.eventId && recommId) {
            recombeeInteractions.addDetailViewInteraction(userId.toString(), event.eventId.toString(), recommId.toString());
        }
    }, [userId, event.eventId, recommId, state.eventId]);

    const handleRsvpClick = async () => {
        try {
            if (rsvpStatus) {
                await participantService.deleteEventParticipant(userId, event.eventId);
                setRsvpStatus(false);
                alert('Successfully un-RSVP\'d');
            } else {
                await participantService.addEventParticipant(userId, event.eventId);
                setRsvpStatus(true);
                alert('Successfully RSVP\'d');
                recombeeInteractions.addPurchaseInteraction(userId.toString(), event.eventId.toString(), recommId.toString());
            }
        } catch (error) {
            alert('An error occurred while processing your RSVP. Please try again.');
            console.error('Error in RSVP process:', error);
        }
    };

  const handleVisitGroup = () => {
    navigate(`/groups/${event.groupId}`);
  };

  // Send recombee detail view when this page is loaded.
  useEffect(() => {
      recombeeInteractions.addDetailViewInteraction(userId, event.eventId, recommId);
  }, [userId, event, recommId]);

  const formattedStartDate = event.eventStartTime.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  const formattedStartTime = event.eventStartTime.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  
  const formattedEndDate = event.eventEndTime.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  const formattedEndTime = event.eventEndTime.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  
  const formattedLocation = event.eventLocationCity + " " + event.eventLocationProvince + " " + event.eventLocationCountry;
  
  return (
    <div className="bg-white">
      {errorMessage && <div className="error-message">{errorMessage}</div>}
      <div className="mx-auto mt-32 px-4 py-16 sm:px-6 sm:py-24 lg:max-w-7xl lg:px-8">
        {/* Product */}
        <div className="lg:grid lg:grid-cols-7 lg:grid-rows-1 lg:gap-x-8 lg:gap-y-10 xl:gap-x-16">
          {/* Product image */}
          <div className="lg:col-span-4 lg:row-end-1">
            <div className="aspect-h-3 aspect-w-4 overflow-hidden rounded-lg bg-gray-100">
              <img src={event.eventImageUrl} alt={event.eventImageAlt} className="object-cover object-center" />
            </div>
          </div>

          {/* Product details */}
          <div className="mx-auto mt-14 max-w-2xl sm:mt-16 lg:col-span-3 lg:row-span-2 lg:row-end-2 lg:mt-0 lg:max-w-none">
            <div className="flex flex-col-reverse">
              <div className="mt-4">
                <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">{event.eventTitle}</h1>
                <h2 id="information-heading" className="sr-only">Event information</h2>
                <p className="mt-2 text-sm text-gray-500">{formattedLocation}</p>
                <p className="mt-2 text-sm text-gray-500">From: {formattedStartDate} at {formattedStartTime}</p>
                <p className="mt-2 text-sm text-gray-500">To: {formattedEndDate} at {formattedEndTime}</p>
              </div>
            </div>
            <div className="mt-8">
              <h2 className="text-sm font-medium text-gray-900">Description</h2>
              <p className="prose prose-sm mt-4 text-gray-500">{event.eventDescription}</p>
            </div>

            <div className="mt-10 grid grid-cols-1 gap-x-6 gap-y-4 sm:grid-cols-2">
              <button
                onClick={handleRsvpClick}
                type="button"
                className="flex w-full items-center justify-center rounded-md border border-transparent bg-pink-600 px-8 py-3 text-base font-medium text-white hover:bg-pink-700 focus:outline-none focus:ring-2 focus:ring-pink-500 focus:ring-offset-2 focus:ring-offset-gray-50"
              >
                 {rsvpStatus === 'Attending' ? 'Un-RSVP' : 'RSVP'}
              </button>
              <button
                onClick={handleVisitGroup}
                type="button"
                className="flex w-full items-center justify-center rounded-md border border-transparent bg-pink-50 px-8 py-3 text-base font-medium text-pink-700 hover:bg-pink-100 focus:outline-none focus:ring-2 focus:ring-pink-500 focus:ring-offset-2 focus:ring-offset-gray-50"
              >
                Visit Group
              </button>
            </div>

            
          </div>

          <div className="mx-auto mt-16 w-full max-w-2xl lg:col-span-4 lg:mt-0 lg:max-w-none">

          </div>
        </div>
      </div>
    </div>
  )
}

export default EventDetailsPage
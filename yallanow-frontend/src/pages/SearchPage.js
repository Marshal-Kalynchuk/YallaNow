import React, {useCallback, useEffect, useState} from 'react';
import { useSearchParams } from 'react-router-dom';
import EventCard from '../components/EventCard';
import feedService from '../api/FeedService';
import { useAuth } from '../AuthContext';

const SearchPage = () => {
  const [events, setEvents] = useState([]);
  const [recommId, setRecommId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [searchParams] = useSearchParams();
  const searchQuery = searchParams.get('query');
  const { currentUser } = useAuth();
  const userId = currentUser?.uid;
  const searchCount = 20;

  const searchEvents = useCallback(async () => {
    if (!searchQuery) return;

    setLoading(true);
    try {
      const data = await feedService.searchEvents(userId, searchCount, searchQuery);
      setEvents(data.recommendations ?? []);
      setRecommId(data.recommId);
    } catch (error) {
      console.error('Error fetching search events:', error);
    } finally {
      setLoading(false);
    }
  }, [userId, searchCount, searchQuery]);

  useEffect(() => {
    searchEvents();
  }, [searchEvents]);


  return (
    <div className="container mx-auto py-8">
      <h2 className="text-2xl font-semibold mb-6">Search Results</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {events.map((event) => (
          <EventCard key={event.eventId} event={event} recommId={recommId} />
        ))}
      </div>
      {loading && <div className="text-center mt-4">Loading...</div>}
    </div>
  );
};

export default SearchPage;


import { useEffect, useRef, useState } from 'react';
import { swedishCities } from '../data/swedishCities';

type LocationSelectorProps = {
    token: string;
    value: string;
    onChange: (location: string) => void;
};

export default function LocationSelector({
                                             value,
                                             onChange
                                         }: LocationSelectorProps) {
    const cities = ['', ...swedishCities];
    const [query, setQuery] = useState('');
    const [open, setOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        function handleOutsideClick(event: MouseEvent) {
            if (
                containerRef.current &&
                !containerRef.current.contains(event.target as Node)
            ) {
                setOpen(false);
                setQuery('');
            }
        }

        document.addEventListener('mousedown', handleOutsideClick);

        return () => {
            document.removeEventListener('mousedown', handleOutsideClick);
        };
    }, []);

    const filtered = cities.filter((name) =>
        name.toLocaleLowerCase('sv-SE').includes(
            query.toLocaleLowerCase('sv-SE').trim()
        )
    );

    function selectLocation(location: string) {
        onChange(location);
        setOpen(false);
        setQuery('');
    }

    return (
        <div className="location-selector" ref={containerRef}>
            <label id="location-label">City</label>

            <button
                type="button"
                className="location-trigger"
                aria-expanded={open}
                aria-haspopup="listbox"
                aria-labelledby="location-label"
                onClick={() => setOpen(!open)}
            >
                <span>{value || 'All Sweden'}</span>
                <span>⌄</span>
            </button>

            {open && (
                <div className="location-dropdown">
                    <input
                        type="search"
                        value={query}
                        onChange={(event) => setQuery(event.target.value)}
                        placeholder="Search city..."
                        aria-label="Search cities"
                        autoFocus
                        onKeyDown={(event) => {
                            if (event.key === 'Escape') {
                                event.stopPropagation();
                                setOpen(false);
                                setQuery('');
                            }

                            if (
                                event.key === 'Enter' &&
                                filtered.length === 1
                            ) {
                                selectLocation(filtered[0]);
                            }
                        }}
                    />

                    <div className="location-options" role="listbox">
                        {filtered.map((name) => (
                            <button
                                key={name || 'all-sweden'}
                                type="button"
                                role="option"
                                aria-selected={value === name}
                                className={
                                    value === name
                                        ? 'location-option selected'
                                        : 'location-option'
                                }
                                onClick={() => selectLocation(name)}
                            >
                                {name || 'All Sweden'}
                            </button>
                        ))}

                        {filtered.length === 0 && (
                            <p className="location-empty">
                                No matching city
                            </p>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

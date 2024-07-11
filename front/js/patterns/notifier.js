export class Notifier
{
    #observers;

    constructor()
    {
        this.#observers = [];
    }

    addObserver(observer)
    {
        this.#observers.push(observer);
    }

    notify()
    {
        for (const observer of this.#observers)
        {
            observer.notify();
        }
    }
}
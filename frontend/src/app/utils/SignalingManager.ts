import { Ticker, Depth } from "./types";

export const BASE_URL = "wss://ws.backpack.exchange/"
export const SPRING_WS_URL = "ws://localhost:8081/ws"

export class SignalingManager {
    private ws: WebSocket;
    private static instance: SignalingManager;
    private bufferedMessages: any[] = [];
    private callbacks: { [type: string]: any[] } = {};
    private id: number;
    private initialized: boolean = false;

    private constructor(private signalingServerUrl?: string) {
        //this.ws = new WebSocket(signalingServerUrl || BASE_URL);
        this.ws = new WebSocket(signalingServerUrl || SPRING_WS_URL);
        this.bufferedMessages = [];
        this.id = 1;
        this.init();
    }
    public static getInstance(siganlingServerUrl?: string) {
        if (!this.instance) {
            this.instance = new SignalingManager(siganlingServerUrl);
        }
        return this.instance;
    }
    sendMessage(message: any) {
        /*const messageToSend = {
            ...message,
            id: this.id++
        }*/
        const messageToSend = {
            ...message,
        }
        if (!this.initialized) {
            this.bufferedMessages.push(messageToSend);
            return;
        }
        this.ws.send(JSON.stringify(messageToSend));
    }
    async registerCallback(type: string, callback: any, id: string) {
        this.callbacks[type] = this.callbacks[type] || [];
        this.callbacks[type].push({ callback, id });
    }
    async deRegisterCallback(type: string, id: string) {
        if(this.callbacks[type]) {
            const index = this.callbacks[type].findIndex(callback => callback.id === id);
            if (index !== -1) {
                this.callbacks[type].splice(index, 1);
            }
        }
    }
    init() {
        this.ws.onopen = () => {
            this.initialized = true;
            this.bufferedMessages.forEach(message => {
               this.ws.send(JSON.stringify(message));
            });
            this.bufferedMessages = [];
        }
        this.ws.onmessage = (event) => {
            const message = JSON.parse(event.data);
            //const type = message.data.e;
            const type = message.e;
            if (this.callbacks[type]) {
                this.callbacks[type].forEach(({ callback }) => {
                    if (type === "ticker") {
                        /*const newTicker: Partial<Ticker> = {
                            lastPrice: message.data.c,
                            high: message.data.h,
                            low: message.data.l,
                            volume: message.data.v,
                            quoteVolume: message.data.V,
                            symbol: message.data.s,
                        }*/
                        const newTicker: Partial<Ticker> = {
                            lastPrice: message.c,
                            high: message.h,
                            low: message.l,
                            volume: message.v,
                            quoteVolume: message.V,
                            symbol: message.s,
                        }
                        console.log(newTicker);
                        callback(newTicker);
                    }
                    if (type === "depth") {
                       /* const updatedBids = message.data.b;
                        const updatedAsks = message.data.a;*/
                        const updatedBids = message.b;
                        const updatedAsks = message.a;
                        callback({bids: updatedBids, asks: updatedAsks});
                        /*const newDepth: Partial<Depth> = {
                            bids: message.data.a,
                            asks: message.data.b,
                            lastUpdateId: message.data.u
                        }
                        callback(newDepth);*/
                    }
                });
            }

        }
    }
}
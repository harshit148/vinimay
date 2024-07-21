import axios, { AxiosRequestConfig } from "axios";
import { Depth, KLine, Ticker, Trade, MarketRequest } from "./types";

const BASE_URL = "https://exchange-proxy.100xdevs.com/api/v1";
const SPRING_BACKEND_BASE_URL = "http://localhost:8080/api/v1";

export async function getTicker(market: string): Promise<Ticker> {
    // const tickers = await getTickers();
    // const ticker = tickers.find(t => t.symbol === market);
    const response = await axios.get(`${SPRING_BACKEND_BASE_URL}/tickers/${market}`)
    const ticker = response.data;
    if (!ticker) {
        throw new Error(`No ticker found for ${market}`);
    }
    return ticker;
}

export async function getTickers():Promise<Ticker[]> {
    //const response = await axios.get(`${BASE_URL}/tickers`);
    const response = await axios.get(`${SPRING_BACKEND_BASE_URL}/tickers`);
    return response.data;
}

export async function getDepth(market: string):Promise<Depth> {
    const config: AxiosRequestConfig = {
        headers: {
            'Content-Type': 'application/json'
        }
    };
    const response = await axios.post(`${SPRING_BACKEND_BASE_URL}/depth`, { market : market } as MarketRequest, config);
    return response.data;
}

export async function getTrades(market: string):Promise<Trade[]> {
    const response = await axios.get(`${BASE_URL}/trades?symbol=${market}`);
    return response.data;
}

export async function getKLines(market: string, interval: string, startTime: number, endTime: number): Promise<KLine[]> {
    const requestKLinesURL = `${BASE_URL}/klines?symbol=${market}&interval=${interval}&startTime=${startTime}&endTime=${endTime}`;
    console.log(requestKLinesURL);
    const response = await axios.get(requestKLinesURL);
    const data: KLine[] = response.data;
    return data.sort((x, y) => (Number(x.end) < Number(y.end) ? -1: 1));
}
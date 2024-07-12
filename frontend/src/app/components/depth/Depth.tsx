"use client";

import { useEffect, useState } from "react";
import {getDepth, getKLines, getTicker, getTrades } from "../../utils/httpClient";
import { BidTable } from "./BidTable";
import { AskTable } from "./AskTable";
import {SignalingManager} from "@/app/utils/SignalingManager";

export function Depth({market} : {market: string}) {
    const [bids, setBids] =  useState<[string, string][]>();
    const [asks, setAsks] = useState<[string, string][]>();
    const [price, setPrice] = useState<string>();
    useEffect(()=> {

        SignalingManager.getInstance().registerCallback("depth", (data: any) => {
            console.log(data);
            let bidsChange = false;
            let asksChange = false;
            setBids(originalBids => {
                const bidsToUpdate = [...(originalBids || [])];
                for ( let i = 0; i < bidsToUpdate.length; i++) {
                    for (let j = 0; j < data.bids.length; j++) {
                        if (bidsToUpdate[i][0] == data.bids[j][0]) {
                            bidsChange = true;
                            if (data.bids[j][1] != 0) {
                                bidsToUpdate[i][1] = data.bids[j][1];
                            }
                            else {
                                bidsToUpdate.splice(i, 1);
                            }
                            break;
                        }
                    }
                }

                return bidsToUpdate;
            });
            setAsks(originalAsks => {
                const asksToUpdate = [...(originalAsks || [])];
                for (let i = 0; i < asksToUpdate.length; i++) {
                    for (let j = 0; j < data.asks.length; j++) {
                        if (asksToUpdate[i][0] == data.asks[j][0]) {
                            asksChange = true;
                            if (data.asks[j][1] != 0) {
                                asksToUpdate[i][1] = data.asks[j][1];
                            }
                            else {
                                asksToUpdate.splice(i, 1);
                            }
                            break;
                        }

                    }
                }
                return asksToUpdate;
            });
            /*if (!bidsChange && !asksChange) {
                getDepth(market).then(d => {
                    setBids(d.bids.reverse());
                    setAsks(d.asks);
                });
            }*/
        }, `DEPTH-${market}`);
        SignalingManager.getInstance().sendMessage({"method": "SUBSCRIBE", "params": [`depth.200ms.${market}`]});
        getDepth(market).then(d => {
           setBids(d.bids.reverse());
           setAsks(d.asks);
       });
        return () => {
            SignalingManager.getInstance().sendMessage({"method": "UNSUBSCRIBE", "params": [`depth.200ms.${market}`]});
            SignalingManager.getInstance().deRegisterCallback("depth", `DEPTH-${market}`);
        }

        //getTicker(market).then(t => setPrice(t.lastPrice));
       //getTrades(market).then(t => setPrice(t[0].price));
       //getKLines(market, "1h",  Math.round(Date.now() / 1000), Math.round(Date.now() / 1000)+3600).then(t=> setPrice(t[0].close));
        //getKLines(market, "1h", 1640099200, 1640100800).then(t=> setPrice(t[0].close));
    }, []);

    return <div>
        <TableHeader />
        {asks && <AskTable asks={asks} />}
        {price && <div> {price}</div>}
        {bids && <BidTable bids={bids} />}
    </div>
}

function TableHeader() {
    return <div className="flex justify-between text-xs">
        <div className="text-white">Price</div>
        <div className="text-slate-500">Size</div>
        <div className="text-slate-500">Total</div>
    </div>
}
/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.w;

import java.util.UUID;
import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.ExileSourceCost;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.Cards;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetCard;
import mage.target.common.TargetCardInYourGraveyard;
import mage.util.CardUtil;

/**
 *
 * @author jeffwadsworth
 */
public class WatchersOfTheDead extends CardImpl {

    public WatchersOfTheDead(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{2}");
        this.subtype.add("Cat");
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Exile Watchers of the Dead: Each opponent chooses 2 cards in his or her graveyard and exiles the rest.
        this.addAbility(new SimpleActivatedAbility(Zone.BATTLEFIELD, new WatchersOfTheDeadEffect(), new ExileSourceCost()));

    }

    public WatchersOfTheDead(final WatchersOfTheDead card) {
        super(card);
    }

    @Override
    public WatchersOfTheDead copy() {
        return new WatchersOfTheDead(this);
    }
}

class WatchersOfTheDeadEffect extends OneShotEffect {

    public WatchersOfTheDeadEffect() {
        super(Outcome.Benefit);
        this.staticText = "Each opponent chooses 2 cards in his or her graveyard and exiles the rest";
    }

    public WatchersOfTheDeadEffect(final WatchersOfTheDeadEffect effect) {
        super(effect);
    }

    @Override
    public WatchersOfTheDeadEffect copy() {
        return new WatchersOfTheDeadEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        MageObject sourceObject = game.getLastKnownInformation(source.getSourceId(), Zone.BATTLEFIELD);
        if (controller != null) {
            for (UUID opponentId : game.getState().getPlayersInRange(controller.getId(), game)) {
                Player opponent = game.getPlayer(opponentId);
                if (opponent != null
                        && opponent != controller) {
                    TargetCard target = new TargetCardInYourGraveyard(2, 2, new FilterCard());
                    target.setNotTarget(true);
                    Cards cardsInGraveyard = opponent.getGraveyard();
                    opponent.choose(outcome, cardsInGraveyard, target, game);
                    if (!cardsInGraveyard.isEmpty()) {
                        for (Card cardInGraveyard : cardsInGraveyard.getCards(game)) {
                            if (!target.getTargets().contains(cardInGraveyard.getId())) {
                                opponent.moveCardToExileWithInfo(cardInGraveyard, CardUtil.getCardExileZoneId(game, source.getId()),
                                        sourceObject.getLogName(), source.getId(), game, Zone.GRAVEYARD, true);
                            }
                        }
                    }

                }
            }
            return true;
        }
        return false;
    }
}
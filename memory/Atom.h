//
// Created by Kev Paloulian on 7/31/2021.
//

#ifndef ARANEAE_REASONER_ATOM_H
#define ARANEAE_REASONER_ATOM_H

#include <string>
#include <parallel_hashmap/phmap.h>

using phmap::flat_hash_map;

namespace araneae {

    struct Atom {
        std::string label;
    };

}

#endif //ARANEAE_REASONER_ATOM_H
